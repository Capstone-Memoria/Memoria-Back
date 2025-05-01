package ac.mju.memoria.backend.domain.diary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.mju.memoria.backend.domain.diary.entity.Reaction;
import ac.mju.memoria.backend.domain.diary.entity.ReactionId;

public interface ReactionRepository extends JpaRepository<Reaction, ReactionId> {
    List<Reaction> findById_Diary_Id(Long diaryId);
}
