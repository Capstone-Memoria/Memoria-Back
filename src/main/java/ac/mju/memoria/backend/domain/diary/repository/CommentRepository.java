package ac.mju.memoria.backend.domain.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.mju.memoria.backend.domain.diary.entity.Comment;
import ac.mju.memoria.backend.domain.diary.entity.Diary;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  Long countByDiary(Diary diary);
}
