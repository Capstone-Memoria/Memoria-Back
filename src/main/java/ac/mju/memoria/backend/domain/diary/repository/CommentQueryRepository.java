package ac.mju.memoria.backend.domain.diary.repository;

import ac.mju.memoria.backend.domain.diary.entity.Comment;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.entity.QComment;
import ac.mju.memoria.backend.domain.diary.entity.QDiary;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static ac.mju.memoria.backend.domain.diary.entity.QComment.comment;
import static ac.mju.memoria.backend.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<Comment> findCommentWithChildren(Long commentId) {
        QComment children = new QComment("children");

        return Optional.ofNullable(
                queryFactory.selectFrom(comment)
                        .leftJoin(comment.children, children).fetchJoin()
                        .where(
                                comment.id.eq(commentId),
                                notDeleted()
                        )
                        .fetchFirst()
        );
    }

    public List<Comment> findCommentsWithChildrenByDiary(Diary diary) {
        QComment children = new QComment("children");
        QDiary diaryEntity = QDiary.diary;

        return queryFactory.selectFrom(comment)
                .leftJoin(comment.diary, diaryEntity).fetchJoin()
                .leftJoin(comment.children, children).fetchJoin()
                .leftJoin(comment.user, user).fetchJoin()
                .leftJoin(comment.lastModifiedBy).fetchJoin()
                .distinct()
                .where(
                        comment.diary.eq(diary),
                        comment.parent.isNull(),
                        notDeleted().or(comment.children.isNotEmpty())
                )
                .fetch();
    }

    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(
                queryFactory.selectFrom(comment)
                        .leftJoin(comment.user, user).fetchJoin()
                        .leftJoin(comment.lastModifiedBy).fetchJoin()
                        .distinct()
                        .where(
                                comment.id.eq(id),
                                notDeleted()
                        )
                        .fetchFirst()
        );
    }

    private static BooleanExpression notDeleted() {
        return comment.isDeleted.isFalse();
    }
}