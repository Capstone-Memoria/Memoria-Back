package ac.mju.memoria.backend.domain.diary.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
  List<Diary> findByDiaryBookOrderByCreatedAtDesc(DiaryBook diaryBook);

  Page<Diary> findByDiaryBook(DiaryBook diaryBook, Pageable pageable);

  Optional<Diary> findByIdAndDiaryBook(Long id, DiaryBook diaryBook);

  List<Diary> findByDiaryBookAndCreatedAtBetween(DiaryBook diaryBook, LocalDateTime start, LocalDateTime end);
}