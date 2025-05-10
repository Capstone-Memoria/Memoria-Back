package ac.mju.memoria.backend.domain.notification.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NewAICommentEvent {
    private final Long aiCommentId;
}
