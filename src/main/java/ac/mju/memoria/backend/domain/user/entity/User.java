package ac.mju.memoria.backend.domain.user.entity;

import ac.mju.memoria.backend.common.auditor.TimeStampedEntity;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.invitation.entity.Invitation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@SuperBuilder
@Table(name = "users")
public class User extends TimeStampedEntity {
    @Id
    private String email;
    private String nickName;
    private String password;

    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private List<DiaryBook> ownedDiaryBooks = new ArrayList<>();

    @OneToMany(mappedBy = "inviteBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitation> invitations = new ArrayList<>();

    public void addOwnedDiaryBook(DiaryBook diaryBook) {
        this.ownedDiaryBooks.add(diaryBook);
        diaryBook.setOwner(this);
    }

}
