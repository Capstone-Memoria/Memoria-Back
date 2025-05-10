package ac.mju.memoria.backend.domain.notification.event;

import ac.mju.memoria.backend.domain.diary.entity.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NewReplyEvent {
    private final Long replyId;
}
