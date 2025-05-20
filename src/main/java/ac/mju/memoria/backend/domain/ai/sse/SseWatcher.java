package ac.mju.memoria.backend.domain.ai.sse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ac.mju.memoria.backend.domain.ai.model.MusicServerNode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseWatcher {
    private final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(Duration.ofMinutes(3)).build();
    private final List<MusicServerNode> nodes;
    private final ObjectMapper objectMapper;
    private final List<Consumer<SseResponse>> listeners = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private EventSource.Factory eventSourceFactory;

    @PostConstruct
    public void init() {
        log.info("Starting SSE Watcher");
        this.eventSourceFactory = EventSources.createFactory(client);
        nodes.forEach(node -> {
            Request request = new Request.Builder()
                    .url(node.getURL() + "/events")
                    .header("Accept", "text/event-stream")
                    .build();
            eventSourceFactory.newEventSource(request, new SseEventListener(request));
        });
    }

    public void addListener(Consumer<SseResponse> listener) {
        listeners.add(listener);
    }

    private class SseEventListener extends EventSourceListener {
        private final Request request;
        private int retryCount = 0;

        public SseEventListener(Request request) {
            this.request = request;
        }

        @Override
        public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
            String url = request.url().toString();
            if (Objects.nonNull(response)) {
                log.error("SSE connection failed for {}: {} - {}", url, response.code(), t);
            } else {
                log.error("SSE connection failed for {}", url, t);
            }

            if (retryCount < 3) {
                retryCount++;
                log.info("Attempting to reconnect to {} in 10 seconds... (Attempt {}/3)", url, retryCount);
                scheduler.schedule(() -> {
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

        @Override
        public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type,
                @NotNull String data) {
            try {
                if (Objects.isNull(type) || !type.equals("job_update"))
                    return;

                TypeReference<SseResponse> typeReference = new TypeReference<>() {
                };
                SseResponse output = objectMapper.readValue(data, typeReference);

                for (Consumer<SseResponse> listener : listeners) {
                    listener.accept(output);
                }
            } catch (Exception e) {
                log.error("Error processing SSE event", e);
            }
        }
    }
}
