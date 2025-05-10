package ac.mju.memoria.backend.domain.notification.event;

import ac.mju.memoria.backend.domain.diary.entity.Reaction;
import ac.mju.memoria.backend.domain.diary.entity.ReactionId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NewReactionEvent {
    private final ReactionId reactionId;
}
