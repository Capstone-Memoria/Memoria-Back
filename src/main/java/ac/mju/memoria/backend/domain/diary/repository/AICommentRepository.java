package ac.mju.memoria.backend.domain.diary.repository;


import ac.mju.memoria.backend.domain.diary.entity.AIComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AICommentRepository extends JpaRepository<AIComment, Long> {
}
