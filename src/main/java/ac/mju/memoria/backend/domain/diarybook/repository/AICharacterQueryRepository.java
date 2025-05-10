package ac.mju.memoria.backend.domain.diarybook.repository;

import ac.mju.memoria.backend.domain.diarybook.entity.AICharacter;
import ac.mju.memoria.backend.domain.diarybook.entity.QAICharacter;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.AICharacterType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ac.mju.memoria.backend.domain.diarybook.entity.QAICharacter.aICharacter;

@RequiredArgsConstructor
@Repository
public class AICharacterQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<AICharacter> findByDiaryBookId(Long diaryBookId) {
        return queryFactory
                .selectFrom(aICharacter)
                .where(
                        aICharacter.type.eq(AICharacterType.DEFAULT)
                                .or(aICharacter.diaryBook.id.eq(diaryBookId))
                )
                .fetch();
    }
}
