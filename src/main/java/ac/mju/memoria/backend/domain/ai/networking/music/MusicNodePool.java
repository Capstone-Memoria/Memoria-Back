package ac.mju.memoria.backend.domain.ai.networking.music;

import ac.mju.memoria.backend.common.utils.JsonUtils;
import ac.mju.memoria.backend.domain.ai.dto.MusicDto;
import ac.mju.memoria.backend.domain.ai.networking.AbstractAsyncNodePool;
import ac.mju.memoria.backend.domain.ai.networking.Node;
import ac.mju.memoria.backend.domain.ai.dto.MusicSseResponse;
import ac.mju.memoria.backend.domain.ai.entity.AiNode;
import ac.mju.memoria.backend.domain.ai.entity.NodeType;
import ac.mju.memoria.backend.domain.ai.repository.AiNodeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;
import ac.mju.memoria.backend.domain.ai.networking.BasicNode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MusicNodePool extends AbstractAsyncNodePool<MusicDto.CreateRequest, byte[]> {
    private final OkHttpClient client = new OkHttpClient();
    private final MusicSseWatcher sseWatcher;
    private final AiNodeRepository aiNodeRepository;
    private final ObjectMapper objectMapper;

    public MusicNodePool(ObjectMapper objectMapper, AiNodeRepository aiNodeRepository) {
        this.objectMapper = objectMapper;
        this.sseWatcher = new MusicSseWatcher(objectMapper);
        this.aiNodeRepository = aiNodeRepository;
    }

    @PostConstruct
    public void initNodes() {
        List<AiNode> musicNodes = aiNodeRepository.findAllByNodeType(NodeType.MUSIC);
        List<Node> nodes = musicNodes.stream()
                .map(aiNode -> new BasicNode(aiNode.getUrl()))
                .collect(Collectors.toList());
        nodes.forEach(this::addNode);
    }

    @Override
    public void start() {
        super.start();
        sseWatcher.init();
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
        Request request = new Request.Builder()
                .url(node.getURL() + "/music/download/" + jobId)
                .build();
        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to fetch job result: {}", response.code());
                throw new RuntimeException("Failed to fetch music result: " + response.message());
            }

            return Objects.requireNonNull(response.body()).bytes();
        } catch (Exception e) {
            log.error("Error fetching job result for jobId {}: {}", jobId, e.getMessage(), e);
            throw new RuntimeException("Error fetching music result: " + e.getMessage(), e);
        }
    }
}
