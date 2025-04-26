package ac.mju.memoria.backend.domain.invitation.entity;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@DiscriminatorValue("DIRECT")
public class DirectInvitation extends Invitation{
    @ManyToOne(fetch = FetchType.LAZY)
    private User inviteTo;

    public static DirectInvitation of(DiaryBook diaryBook, User inviteTo, User inviteBy) {
        return DirectInvitation.builder()
                .inviteTo(inviteTo)
                .diaryBook(diaryBook)
                .inviteBy(inviteBy)
                .build();
    }
}