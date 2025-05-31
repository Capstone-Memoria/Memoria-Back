package ac.mju.memoria.backend.domain.diarybook.repository;

import ac.mju.memoria.backend.domain.diarybook.entity.UserDiaryBookPin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDiaryBookPinRepository extends JpaRepository<UserDiaryBookPin, Long> {
  Optional<UserDiaryBookPin> findByUserEmailAndDiaryBookId(String email, Long diaryBookId);
}