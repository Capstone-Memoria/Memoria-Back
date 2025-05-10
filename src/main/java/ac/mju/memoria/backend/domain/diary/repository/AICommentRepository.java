package ac.mju.memoria.backend.domain.diary.repository;


import ac.mju.memoria.backend.domain.diary.entity.AIComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AICommentRepository extends JpaRepository<AIComment, Long> {
    List<AIComment> findByDiaryId(Long diaryId);
}
