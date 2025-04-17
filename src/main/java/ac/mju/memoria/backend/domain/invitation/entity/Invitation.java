package ac.mju.memoria.backend.domain.invitation.entity;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.invitation.entity.enums.InvitationStatus;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Invitation extends UserStampedEntity {
    @Id @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_book_id")
    private DiaryBook diaryBook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_email")
    private User inviteBy;

    public void canInviteBy(UserDetails user) {
        if(diaryBook.isAdmin(user.getUser())) {
            return;
        }

        throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }
}
