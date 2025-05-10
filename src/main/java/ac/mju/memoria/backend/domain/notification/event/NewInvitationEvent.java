package ac.mju.memoria.backend.domain.notification.event;

import ac.mju.memoria.backend.domain.invitation.entity.DirectInvitation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NewInvitationEvent {
    private final Long invitationId;
}
