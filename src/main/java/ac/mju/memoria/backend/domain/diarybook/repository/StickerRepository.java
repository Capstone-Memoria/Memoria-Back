package ac.mju.memoria.backend.domain.diarybook.repository;


import ac.mju.memoria.backend.domain.diarybook.entity.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StickerRepository extends JpaRepository<Sticker, String> {
    void deleteAllByDiaryBookId(Long id);

    List<Sticker> findAllByDiaryBookId(Long id);
}
