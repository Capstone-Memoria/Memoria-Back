package ac.mju.memoria.backend.domain.ai.networking.music;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ac.mju.memoria.backend.common.utils.JsonUtils;
import ac.mju.memoria.backend.domain.ai.dto.MusicDto;
import ac.mju.memoria.backend.domain.ai.dto.MusicSseResponse;
import ac.mju.memoria.backend.domain.ai.networking.AbstractAsyncNodePool;
import ac.mju.memoria.backend.domain.ai.networking.DBNode;
import ac.mju.memoria.backend.domain.ai.networking.Node;
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
}
