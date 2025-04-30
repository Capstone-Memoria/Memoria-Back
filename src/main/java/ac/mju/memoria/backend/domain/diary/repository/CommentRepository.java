package ac.mju.memoria.backend.domain.diary.repository;

import ac.mju.memoria.backend.domain.diary.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
