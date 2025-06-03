package ac.mju.memoria.backend.domain.ai.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import ac.mju.memoria.backend.domain.diary.dto.DiaryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class QueueDto {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(description = "큐 아이템 정보")
  @Builder
  public static class QueueItemResponse {
    @Schema(description = "큐 아이템 고유 식별자")
    private UUID uuid;

    @Schema(description = "큐 아이템 생성 시간")
    private LocalDateTime timestamp;

    @Schema(description = "요청 처리 시작 시간")
    private LocalDateTime requestedAt;

    @Schema(description = "연관된 다이어리")
    private DiaryDto.DiaryResponse diary;

    @Schema(description = "재시도 남은 횟수")
    private int retryCountDown;

    @Schema(description = "처리 중인 노드 URL")
    private String processingNodeUrl;

    @Schema(description = "요청 상태")
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(description = "큐 목록 조회 응답")
  public static class QueueListResponse {
    @Schema(description = "대기 중인 큐 아이템 목록")
    private List<QueueItemResponse> pendingItems;

    @Schema(description = "처리 중인 큐 아이템 목록")
    private List<QueueItemResponse> processingItems;

    @Schema(description = "전체 대기 큐 크기")
    private int totalPendingCount;

    @Schema(description = "전체 처리 중 작업 수")
    private int totalProcessingCount;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(description = "큐 아이템 순서 변경 요청")
  public static class ReorderQueueRequest {
    @Schema(description = "이동할 큐 아이템 UUID")
    private UUID itemUuid;

    @Schema(description = "새로운 위치 인덱스 (0부터 시작)")
    private int newPosition;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(description = "큐 아이템 삭제 요청")
  public static class DeleteQueueItemRequest {
    @Schema(description = "삭제할 큐 아이템 UUID")
    private UUID itemUuid;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(description = "큐 아이템 삭제 응답")
  public static class DeleteQueueItemResponse {
    @Schema(description = "삭제 성공 여부")
    private boolean success;

    @Schema(description = "삭제된 아이템 UUID")
    private UUID deletedItemUuid;

    @Schema(description = "메시지")
    private String message;
  }
}