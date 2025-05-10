package ac.mju.memoria.backend.domain.diarybook.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.mju.memoria.backend.domain.diarybook.entity.AICharacter;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;

public interface AICharacterRepository extends JpaRepository<AICharacter, Long> {
    Optional<AICharacter> findByDiaryBookAndId(DiaryBook diaryBook, Long characterId);

    List<AICharacter> findAllByDiaryBook(DiaryBook diaryBook);
}
