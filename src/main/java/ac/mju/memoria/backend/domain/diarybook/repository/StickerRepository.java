package ac.mju.memoria.backend.domain.diarybook.repository;


import ac.mju.memoria.backend.domain.diarybook.entity.Sticker;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StickerRepository extends JpaRepository<Sticker, Long> {
    void deleteAllByDiaryBookId(Long id);
}
