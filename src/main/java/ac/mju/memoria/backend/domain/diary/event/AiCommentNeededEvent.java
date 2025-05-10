package ac.mju.memoria.backend.domain.diary.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AiCommentNeededEvent extends ApplicationEvent {
    private final Long diaryId;
    public AiCommentNeededEvent(Object source, Long diaryId) {
        super(source);
        this.diaryId = diaryId;
    }

    public static AiCommentNeededEvent of(Object caller, Long diaryId) {
        return new AiCommentNeededEvent(caller, diaryId);
    }
}
