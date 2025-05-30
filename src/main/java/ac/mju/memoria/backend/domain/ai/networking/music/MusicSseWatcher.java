package ac.mju.memoria.backend.domain.ai.networking.music;

import ac.mju.memoria.backend.domain.ai.networking.Node;
import ac.mju.memoria.backend.domain.ai.dto.MusicSseResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class MusicSseWatcher implements SseWatcher<MusicSseResponse> {
    private final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(Duration.ofMinutes(3)).build();
    private EventSource.Factory eventSourceFactory;
    private final ScheduledExecutorService retryScheduler = Executors.newScheduledThreadPool(1);
    private final List<Node> listeningNodes = new ArrayList<>();
    private final List<SseListener<MusicSseResponse>> listeners = new ArrayList<>();
    private final Map<Node, EventSource> eventSources = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @PostConstruct
    @Override
    public void init() {
        log.info("Starting MusicSSEWatcher");
        this.eventSourceFactory = EventSources.createFactory(client);
    }

    @Override
    public void addListener(SseListener<MusicSseResponse> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(SseListener<MusicSseResponse> listener) {
        listeners.remove(listener);
    }

    @Override
    public void addNode(Node node) {
        if (listeningNodes.contains(node)) {
            log.warn("Node {} is already being listened to", node.getURL());
            return;
        }

        listeningNodes.add(node);
        Request request = new Request.Builder()
                .url(node.getURL() + "/events")
                .header("Accept", "text/event-stream")
                .build();
        SseEventListener eventListener = new SseEventListener(objectMapper, request, node);
        EventSource eventSource = eventSourceFactory.newEventSource(request, eventListener);
        eventSources.put(node, eventSource);
    }

    @Override
    public void removeNode(Node node) {
        listeningNodes.remove(node);
        EventSource eventSource = eventSources.remove(node);
        if (eventSource != null) {
            eventSource.cancel();
            log.info("Removed SSE listener for node: {}", node.getURL());
        } else {
            log.warn("No SSE listener found for node: {}", node.getURL());
        }
    }

    @RequiredArgsConstructor
    private class SseEventListener extends EventSourceListener {
        private final ObjectMapper objectMapper;
        private final Request request;
        private final Node node;
        private int retryCount = 0;

        @Override
        public void onClosed(@NotNull EventSource eventSource) {
            log.info("SSE connection closed: {}", request.url());
            removeNode(node);
        }

        @Override
        public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
            try {
                if (Objects.isNull(type) || !type.equals("job_update"))
                    return;

                TypeReference<MusicSseResponse> typeReference = new TypeReference<>() {
                };
                MusicSseResponse output = objectMapper.readValue(data, typeReference);

                log.info("Received SSE event: {}", output);
                for (SseListener<MusicSseResponse> listener : listeners) {
                    listener.handleResponse(node, output);
                }

            }catch (Exception e) {
                log.error("Error processing SSE event: {}", e.getMessage(), e);
            }
        }

        @Override
        public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
            String url = request.url().toString();

            if (Objects.nonNull(response)) {
                log.error("SSE connection failed for {}: {}", url, response.code(), t);
            } else {
                log.error("SSE connection failed for {}", url, t);
            }

            if (retryCount < 3) {
                retryCount++;
                log.info("Attempting to reconnect to {} in 10 seconds... (Attempt {}/3)", url, retryCount);
                retryScheduler.schedule(() -> {
                    log.info("Retrying connection to {}...", url);
                    eventSourceFactory.newEventSource(request, this);
                }, 10, TimeUnit.SECONDS);
            } else {
                log.error("Failed to connect to {} after 3 attempts. Giving up.", url);
            }
        }

        @Override
        public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
            log.info("SSE connection opened: {}", response.request().url());
            retryCount = 0; // 연결 성공 시 재시도 횟수 초기화
        }
    }
}
