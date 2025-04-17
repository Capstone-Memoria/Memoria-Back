package ac.mju.memoria.backend.domain.diarybook.entity;

import ac.mju.memoria.backend.common.auditor.TimeStampedEntity;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.MemberPermission;
import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class DiaryBookMember extends TimeStampedEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private DiaryBook diaryBook;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private MemberPermission permission;

    public void makeRelationWIthDiaryBook(DiaryBook diaryBook) {
        if(Objects.isNull(diaryBook)) {
            diaryBook.getMembers().remove(this);
            this.setDiaryBook(diaryBook);
            return;
        }

        diaryBook.getMembers().add(this);
        this.setDiaryBook(diaryBook);
    }

    public static DiaryBookMember of(DiaryBook diaryBook, User user) {
        return DiaryBookMember.builder()
                .diaryBook(diaryBook)
                .user(user)
                .permission(MemberPermission.MEMBER)
                .build();
    }
}
