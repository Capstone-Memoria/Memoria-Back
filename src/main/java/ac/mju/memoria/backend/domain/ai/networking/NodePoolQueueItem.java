package ac.mju.memoria.backend.domain.ai.networking;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import lombok.Builder;
import lombok.Data;

/**
 * NodePool에서 사용되는 큐 아이템을 나타내는 클래스입니다.
 * 각 아이템은 요청, 재시도 횟수, 응답 처리를 위한 CompletableFuture와 ResponseHandler,
 * 그리고 감사를 위한 메타데이터(UUID, 타임스탬프 등)를 포함합니다.
 *
 * @param <REQ> 요청 객체의 타입
 * @param <RES> 응답 객체의 타입
 */
@Data
@Builder
public class NodePoolQueueItem<REQ, RES> {
    // === Metadata ===
    /**
     * 원본 요청 객체입니다.
     */
    private final REQ request;
    /**
     * 남은 재시도 횟수입니다.
     */
    private int retryCountDown;

    // === Response Handling ===
    /**
     * 비동기 응답을 처리하기 위한 CompletableFuture 객체입니다.
     */
    private CompletableFuture<RES> response;
    /**
     * 응답을 직접 처리하기 위한 핸들러입니다. (선택 사항)
     */
    @Nullable
    private ResponseHandler<RES> responseHandler;

    // === Auditing ===
    /**
     * 아이템의 고유 식별자입니다.
     */
    private final UUID uuid;
    /**
     * 아이템 생성 타임스탬프입니다.
     */
    private final LocalDateTime timestamp;
    /**
     * 요청이 실제로 처리 시작된 시간입니다.
     */
    private LocalDateTime requestedAt;
    /**
     * 요청을 처리하는 Node 객체입니다.
     */
    private Node requestProcessor;
    /**
     * 연관된 다이어리 ID입니다. (선택 사항)
     */
    @Nullable
    private Long diaryId;

    /**
     * 주어진 요청으로부터 새로운 NodePoolQueueItem을 생성합니다.
     * UUID, 현재 타임스탬프, 기본 재시도 횟수(1), 새로운 CompletableFuture로 초기화됩니다.
     *
     * @param request 생성할 요청 객체
     * @param <REQ>   요청 객체의 타입
     * @param <RES>   응답 객체의 타입
     * @return 생성된 NodePoolQueueItem 객체
     */
    public static <REQ, RES> NodePoolQueueItem<REQ, RES> from(REQ request) {
        return NodePoolQueueItem.<REQ, RES>builder()
                .uuid(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .request(request)
                .retryCountDown(1)
                .response(new CompletableFuture<>())
                .build();
    }

    /**
     * 주어진 요청과 다이어리 ID로부터 새로운 NodePoolQueueItem을 생성합니다.
     * UUID, 현재 타임스탬프, 기본 재시도 횟수(1), 새로운 CompletableFuture로 초기화됩니다.
     *
     * @param request 생성할 요청 객체
     * @param diaryId 연관된 다이어리 ID
     * @param <REQ>   요청 객체의 타입
     * @param <RES>   응답 객체의 타입
     * @return 생성된 NodePoolQueueItem 객체
     */
    public static <REQ, RES> NodePoolQueueItem<REQ, RES> from(REQ request, Long diaryId) {
        return NodePoolQueueItem.<REQ, RES>builder()
                .uuid(UUID.randomUUID())
                .timestamp(LocalDateTime.now())
                .request(request)
                .retryCountDown(1)
                .response(new CompletableFuture<>())
                .diaryId(diaryId)
                .build();
    }
}
