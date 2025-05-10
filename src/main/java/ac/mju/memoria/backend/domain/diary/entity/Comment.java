package ac.mju.memoria.backend.domain.diary.entity;

import java.util.List;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.diary.entity.enums.CommentType;
import ac.mju.memoria.backend.domain.diarybook.entity.AICharacter;
import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Comment extends UserStampedEntity {
    @Id @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private String content;

    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private CommentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_character_id")
    private AICharacter aiCharacter;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children;
    
    public static Comment createUserComment(Diary diary, User user) {
        return Comment.builder()
                .diary(diary)
                .user(user)
                .type(CommentType.USER)
                .build();
    }
    
    public static Comment createAIComment(Diary diary, AICharacter character) {
        return Comment.builder()
                .diary(diary)
                .aiCharacter(character)
                .type(CommentType.AI)
                .build();
    }
}
