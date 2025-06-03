package ac.mju.memoria.backend.domain.ai.listener;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ac.mju.memoria.backend.domain.ai.service.AiConfigService;
import ac.mju.memoria.backend.domain.diary.event.DiaryDeletedEvent;

@ExtendWith(MockitoExtension.class)
class AiRequestCancelEventListenerTest {

  @Mock
  private AiConfigService aiConfigService;

  @InjectMocks
  private AiRequestCancelEventListener eventListener;

  @Test
  @DisplayName("다이어리 삭제 이벤트 처리 - AI 요청 취소 성공")
  void handleDiaryDeletedEvent_SuccessfullyCancelsRequests() {
    // given
    Long diaryId = 123L;
    DiaryDeletedEvent event = new DiaryDeletedEvent(this, diaryId);

    when(aiConfigService.cancelAiRequestsByDiaryId(eq(diaryId)))
        .thenReturn(3);

    // when
    eventListener.handleDiaryDeletedEvent(event);

    // then
    verify(aiConfigService, times(1)).cancelAiRequestsByDiaryId(eq(diaryId));
  }

  @Test
  @DisplayName("다이어리 삭제 이벤트 처리 - 취소할 요청이 없는 경우")
  void handleDiaryDeletedEvent_NoRequestsToCancel() {
    // given
    Long diaryId = 123L;
    DiaryDeletedEvent event = new DiaryDeletedEvent(this, diaryId);

    when(aiConfigService.cancelAiRequestsByDiaryId(eq(diaryId)))
        .thenReturn(0);

    // when
    eventListener.handleDiaryDeletedEvent(event);

    // then
    verify(aiConfigService, times(1)).cancelAiRequestsByDiaryId(eq(diaryId));
  }

  @Test
  @DisplayName("다이어리 삭제 이벤트 처리 - 예외 발생 시 로그만 남기고 정상 진행")
  void handleDiaryDeletedEvent_HandlesExceptionGracefully() {
    // given
    Long diaryId = 123L;
    DiaryDeletedEvent event = new DiaryDeletedEvent(this, diaryId);

    when(aiConfigService.cancelAiRequestsByDiaryId(eq(diaryId)))
        .thenThrow(new RuntimeException("Test exception"));

    // when & then (예외가 발생해도 정상적으로 진행되어야 함)
    eventListener.handleDiaryDeletedEvent(event);

    verify(aiConfigService, times(1)).cancelAiRequestsByDiaryId(eq(diaryId));
  }
}