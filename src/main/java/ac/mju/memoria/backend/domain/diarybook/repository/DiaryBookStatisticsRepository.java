package ac.mju.memoria.backend.domain.diarybook.repository;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookStatistics;

public interface DiaryBookStatisticsRepository extends JpaRepository<DiaryBookStatistics, Long> {
  Optional<DiaryBookStatistics> findByDiaryBookAndTargetMonth(DiaryBook diaryBook, YearMonth targetMonth);
}