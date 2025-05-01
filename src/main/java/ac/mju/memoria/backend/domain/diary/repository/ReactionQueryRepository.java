package ac.mju.memoria.backend.domain.diary.repository;

import static ac.mju.memoria.backend.domain.diary.entity.QReaction.reaction;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import ac.mju.memoria.backend.domain.diary.entity.Reaction;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class ReactionQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<Reaction> findByDiaryId(Long diaryId) {
        return jpaQueryFactory.selectFrom(reaction)
                .leftJoin(reaction.id.user).fetchJoin()
                .where(reaction.id.diary.id.eq(diaryId))
                .fetch();
    }
}
