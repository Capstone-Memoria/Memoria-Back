package ac.mju.memoria.backend.domain.diarybook.repository;


import ac.mju.memoria.backend.domain.diarybook.entity.stickers.AbstractSticker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StickerRepository extends JpaRepository<AbstractSticker, String> {
    void deleteAllByDiaryBookId(Long id);

    List<AbstractSticker> findAllByDiaryBookId(Long id);
}
