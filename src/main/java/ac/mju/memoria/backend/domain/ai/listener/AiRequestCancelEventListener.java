package ac.mju.memoria.backend.domain.ai.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import ac.mju.memoria.backend.domain.ai.service.AiConfigService;
import ac.mju.memoria.backend.domain.diary.event.DiaryDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 다이어리 삭제 시 AI 요청을 취소하는 이벤트 리스너입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiRequestCancelEventListener {
  private final AiConfigService aiConfigService;

  /**
   * 다이어리 삭제 이벤트를 처리하여 관련된 AI 요청들을 취소합니다.
   * 트랜잭션 커밋 후에 비동기로 실행됩니다.
   *
   * @param event 다이어리 삭제 이벤트
   */
  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleDiaryDeletedEvent(DiaryDeletedEvent event) {
    log.info("Handling diary deleted event for diary ID: {}", event.getDiaryId());

    try {
      int cancelledRequests = aiConfigService.cancelAiRequestsByDiaryId(event.getDiaryId());

      if (cancelledRequests > 0) {
        log.info("Successfully cancelled {} AI requests for deleted diary ID: {}",
            cancelledRequests, event.getDiaryId());
      } else {
        log.debug("No AI requests found to cancel for diary ID: {}", event.getDiaryId());
      }
    } catch (Exception e) {
      log.error("Error occurred while cancelling AI requests for diary ID: {}. Error: {}",
          event.getDiaryId(), e.getMessage(), e);
    }
  }
}