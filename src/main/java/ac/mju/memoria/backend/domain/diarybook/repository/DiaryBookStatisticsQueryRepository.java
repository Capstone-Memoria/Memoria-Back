package ac.mju.memoria.backend.domain.diarybook.repository;

import static ac.mju.memoria.backend.domain.diary.entity.QDiary.diary;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiaryBookStatisticsQueryRepository {

  private final JPAQueryFactory queryFactory;
  private static final int RANKING_LIMIT = 5; // 랭킹 표시 개수 제한

  public List<Diary> findTopDiariesByCommentCount(DiaryBook diaryBook) {
    return queryFactory.selectFrom(diary)
        .where(diary.diaryBook.eq(diaryBook))
        .orderBy(diary.comments.size().desc(), diary.createdAt.desc())
        .limit(RANKING_LIMIT)
        .fetch();
  }

  public List<Diary> findTopDiariesByReactionCount(DiaryBook diaryBook) {
    return queryFactory.selectFrom(diary)
        .where(diary.diaryBook.eq(diaryBook))
        .orderBy(diary.reactions.size().desc(), diary.createdAt.desc())
        .limit(RANKING_LIMIT)
        .fetch();
  }
}