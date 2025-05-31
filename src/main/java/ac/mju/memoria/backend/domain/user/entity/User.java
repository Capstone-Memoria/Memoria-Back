package ac.mju.memoria.backend.domain.user.entity;

import ac.mju.memoria.backend.common.auditor.TimeStampedEntity;
import ac.mju.memoria.backend.domain.diary.entity.Comment;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.UserDiaryBookPin;
import ac.mju.memoria.backend.domain.invitation.entity.Invitation;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Table(name = "users")
public class User extends TimeStampedEntity {
    @Id
    private String email;
    private String nickName;
    private String password;

    @OneToMany(mappedBy = "owner")
    @Builder.Default
    private List<DiaryBook> ownedDiaryBooks = new ArrayList<>();

    @OneToMany(mappedBy = "inviteBy", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Invitation> invitations = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Diary> ownedDiaries = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<UserDiaryBookPin> diaryBookPins = new ArrayList<>();

    public void addOwnedDiaryBook(DiaryBook diaryBook) {
        this.ownedDiaryBooks.add(diaryBook);
        diaryBook.setOwner(this);
    }

    public void addDiary(Diary diary) {
        this.ownedDiaries.add(diary);
        diary.setAuthor(this);
    }

    public void unproxy() {
        ownedDiaryBooks = new ArrayList<>(ownedDiaryBooks);
        ownedDiaries = new ArrayList<>(ownedDiaries);
        invitations = new ArrayList<>(invitations);
        diaryBookPins = new ArrayList<>(diaryBookPins);
    }
}
