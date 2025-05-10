package ac.mju.memoria.backend.domain.diary.entity;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.file.entity.Image;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Diary extends UserStampedEntity {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_book_id")
    private DiaryBook diaryBook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_email")
    private User author;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public void addImage(Image image) {
        this.images.add(image);
        image.setDiary(this);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setDiary(this);
    }

    public boolean canUpdateAndDelete(User user) {
        if (user.getEmail().equals(author.getEmail())) {
            return true;
        }

        throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }

    public boolean isDiaryBookMember(User user) {
        if (this.diaryBook.isMember(user)) {
            return true;
        }
        throw new RestException(ErrorCode.AUTH_FORBIDDEN);
    }
}