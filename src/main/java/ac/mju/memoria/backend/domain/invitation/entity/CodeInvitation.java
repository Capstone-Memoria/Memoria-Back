package ac.mju.memoria.backend.domain.invitation.entity;


import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@DiscriminatorValue("CODE")
public class CodeInvitation extends Invitation {
    @Column(unique = true)
    private String inviteCode;

    private LocalDateTime expiresAt;

    public void renewInviteCode(int hours) {
        inviteCode = UUID.randomUUID().toString();
        expiresAt = LocalDateTime.now().plusHours(hours);
    }

    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiresAt);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public static CodeInvitation of(DiaryBook diaryBook, User inviter, LocalDateTime expiresAt) {
        return CodeInvitation.builder()
                .inviteCode(UUID.randomUUID().toString())
                .expiresAt(expiresAt)
                .diaryBook(diaryBook)
                .inviteBy(inviter)
                .build();
    }
}
