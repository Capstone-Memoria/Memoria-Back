package ac.mju.memoria.backend.domain.ai.networking;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
@Builder
public class NodePoolQueueItem<REQ, RES> {
    private final String uuid;
    private final REQ request;
    private int retryCountDown;

    private CompletableFuture<RES> response;

    public static <REQ,RES> NodePoolQueueItem<REQ, RES> from(REQ request) {
        return NodePoolQueueItem.<REQ, RES>builder()
                .uuid(UUID.randomUUID().toString())
                .request(request)
                .retryCountDown(1)
                .response(new CompletableFuture<>())
                .build();
    }
}
