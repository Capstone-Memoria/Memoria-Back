package ac.mju.memoria.backend.domain.diary.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class DiaryDeletedEvent extends ApplicationEvent {
  private final Long diaryId;

  public DiaryDeletedEvent(Object source, Long diaryId) {
    super(source);
    this.diaryId = diaryId;
  }
}