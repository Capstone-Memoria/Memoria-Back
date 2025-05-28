package ac.mju.memoria.backend.domain.diary.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class DiaryCreatedEvent extends ApplicationEvent {
  private final Long diaryBookId;

  public DiaryCreatedEvent(Object source, Long diaryBookId) {
    super(source);
    this.diaryBookId = diaryBookId;
  }
}