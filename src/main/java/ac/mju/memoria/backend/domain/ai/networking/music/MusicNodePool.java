package ac.mju.memoria.backend.domain.ai.networking.music;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ac.mju.memoria.backend.domain.diary.dto.DiaryDto;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ac.mju.memoria.backend.common.utils.JsonUtils;
import ac.mju.memoria.backend.domain.ai.dto.MusicDto;
import ac.mju.memoria.backend.domain.ai.dto.MusicSseResponse;
import ac.mju.memoria.backend.domain.ai.dto.QueueDto;
import ac.mju.memoria.backend.domain.ai.networking.AbstractAsyncNodePool;
import ac.mju.memoria.backend.domain.ai.networking.DBNode;
import ac.mju.memoria.backend.domain.ai.networking.Node;
import ac.mju.memoria.backend.domain.ai.networking.NodePoolQueueItem;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Slf4j
@Component
@RequiredArgsConstructor
public class MusicNodePool extends AbstractAsyncNodePool<MusicDto.CreateRequest, byte[]> {
    private final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
    private final WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(50 * 1024 * 1024)) // 50MB
            .build();
    private final MusicSseWatcher sseWatcher;
    private final ObjectMapper objectMapper;
    private final DiaryRepository diaryRepository;

    public Optional<DBNode> getNodeById(Long id) {
        return getNodes().stream()
                .filter(node -> ((DBNode) node).getId().equals(id))
                .findFirst()
                .map(DBNode.class::cast);
    }

    @PostConstruct
    @Override
    public void start() {
        super.start();
        sseWatcher.addListener(this::handleSseResponse);
    }

    @Override
    public void addNode(@NonNull Node node) {
        super.addNode(node);
        sseWatcher.addNode(node);
    }

    @Override
    public void removeNode(@NonNull Node node) {
        super.removeNode(node);
        sseWatcher.removeNode(node);
    }

    private void handleSseResponse(Node sourceNode, MusicSseResponse response) {
        log.info("Received SSE response from node {}: {}", sourceNode.getURL(), response);

        response.getJobs().entrySet().stream()
                .filter(it -> it.getValue().getStatus().equals("completed"))
                .map(Map.Entry::getKey)
                .forEach(this::onJobCompleted);
    }

    @Override
    @SneakyThrows
    protected String handleSubmitRequest(MusicDto.CreateRequest request, Node node) {
        RequestBody requestBody = RequestBody.create(
                JsonUtils.toJson(request),
                MediaType.parse("application/json"));
        Request httpRequest = new Request.Builder()
                .url(node.getURL() + "/generate-music-async/")
                .post(requestBody)
                .build();

        try (okhttp3.Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("Request failed: {}", response.code());
                throw new RuntimeException("Music generation request failed: " + response.message());
            }

            String body = Objects.requireNonNull(response.body()).string();

            JsonNode treeResult = objectMapper.readTree(body);
            return treeResult.get("job_id").asText();
        }
    }

    @Override
    protected byte[] fetchJobResult(String jobId, Node node) {
        try {
            return webClient.get()
                    .uri(node.getURL() + "/music/download/" + jobId)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching job result for jobId {}: {}", jobId, e.getMessage(), e);
            throw new RuntimeException("Error fetching music result: " + e.getMessage(), e);
        }
    }

    /**
     * 현재 큐 상태를 조회합니다.
     * 
     * @return 큐 상태 정보
     */
    public QueueDto.QueueListResponse getQueueStatus() {
        List<NodePoolQueueItem<MusicDto.CreateRequest, byte[]>> pendingItems = getRequestQueueItems();
        List<NodePoolQueueItem<MusicDto.CreateRequest, byte[]>> processingItems = getPendingJobItems();

        List<QueueDto.QueueItemResponse> pendingResponse = pendingItems.stream()
                .map(this::convertToQueueItemResponse)
                .collect(Collectors.toList());

        List<QueueDto.QueueItemResponse> processingResponse = processingItems.stream()
                .map(this::convertToQueueItemResponse)
                .collect(Collectors.toList());

        return new QueueDto.QueueListResponse(
                pendingResponse,
                processingResponse,
                pendingItems.size(),
                processingItems.size());
    }

    /**
     * 큐 아이템의 순서를 변경합니다.
     * 
     * @param uuid        이동할 아이템의 UUID
     * @param newPosition 새로운 위치
     * @return 순서 변경 성공 여부
     */
    public boolean reorderQueueItem(UUID uuid, int newPosition) {
        return super.reorderQueueItem(uuid, newPosition);
    }

    /**
     * 큐에서 특정 아이템을 삭제합니다.
     * 
     * @param uuid 삭제할 아이템의 UUID
     * @return 삭제 성공 여부
     */
    public boolean deleteQueueItem(UUID uuid) {
        // 먼저 대기 큐에서 삭제 시도
        boolean removedFromQueue = removeQueueItemByUuid(uuid);
        if (removedFromQueue) {
            log.info("Removed queue item with UUID: {}", uuid);
            return true;
        }

        // 처리 중인 작업에서 삭제 시도
        boolean removedFromPending = removePendingJobByUuid(uuid);
        if (removedFromPending) {
            log.info("Cancelled pending job with UUID: {}", uuid);
            return true;
        }

        return false;
    }

    /**
     * NodePoolQueueItem을 QueueItemResponse로 변환합니다.
     */
    @Transactional(readOnly = true)
    protected QueueDto.QueueItemResponse convertToQueueItemResponse(
            NodePoolQueueItem<MusicDto.CreateRequest, byte[]> item) {
        Diary foundDiary = Optional.ofNullable(item.getDiaryId())
                .flatMap(diaryRepository::findById)
                .orElse(null);

        QueueDto.QueueItemResponse response = QueueDto.QueueItemResponse.builder()
                .uuid(item.getUuid())
                .timestamp(item.getTimestamp())
                .requestedAt(item.getRequestedAt())
                .retryCountDown(item.getRetryCountDown())
                .diary(
                        foundDiary != null ? DiaryDto.DiaryResponse.from(foundDiary) : null
                )
                .build();

        if (item.getRequestProcessor() != null) {
            response.setProcessingNodeUrl(item.getRequestProcessor().getURL());
            response.setStatus("PROCESSING");
        } else {
            response.setStatus("PENDING");
        }

        return response;
    }
}
