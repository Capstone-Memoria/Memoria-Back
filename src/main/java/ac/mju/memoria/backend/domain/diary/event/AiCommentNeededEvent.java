package ac.mju.memoria.backend.domain.diary.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationEvent;

import java.util.Optional;

@Getter
public class AiCommentNeededEvent extends ApplicationEvent {
    private final Long diaryId;
    @Nullable
    private final Long desiredCharacterId;

    public AiCommentNeededEvent(Object source, Long diaryId, @Nullable Long desiredCharacterId) {
        super(source);
        this.diaryId = diaryId;
        this.desiredCharacterId = desiredCharacterId;
    }

    public static AiCommentNeededEvent of(Object caller, Long diaryId) {
        return new AiCommentNeededEvent(caller, diaryId, null);
    }

    public static AiCommentNeededEvent of(Object caller, Long diaryId, Long desiredCharacterId) {
        return new AiCommentNeededEvent(caller, diaryId, desiredCharacterId);
    }
}
