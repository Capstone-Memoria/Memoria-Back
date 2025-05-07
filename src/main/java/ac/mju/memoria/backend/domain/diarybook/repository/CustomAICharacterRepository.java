package ac.mju.memoria.backend.domain.diarybook.repository;

import ac.mju.memoria.backend.domain.diarybook.entity.CustomAICharacter;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomAICharacterRepository extends JpaRepository<CustomAICharacter, Long> {
    Optional<CustomAICharacter> findByDiaryBookAndId(DiaryBook diaryBook, Long characterId);
}
