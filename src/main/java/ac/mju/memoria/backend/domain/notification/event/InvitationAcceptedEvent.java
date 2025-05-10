package ac.mju.memoria.backend.domain.notification.event;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookMember;
import ac.mju.memoria.backend.domain.invitation.entity.Invitation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvitationAcceptedEvent {
    private final Long newMemberId;
    private final Long invitationId;
}
