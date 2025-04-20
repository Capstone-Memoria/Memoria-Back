package ac.mju.memoria.backend.domain.diary.repository;

import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.entity.Reaction;
import ac.mju.memoria.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByDiaryAndUser(Diary diary, User user);
}
