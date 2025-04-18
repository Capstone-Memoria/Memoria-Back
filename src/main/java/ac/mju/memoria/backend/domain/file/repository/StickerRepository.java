package ac.mju.memoria.backend.domain.file.repository;


import ac.mju.memoria.backend.domain.file.entity.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StickerRepository extends JpaRepository<Sticker, String> {
}
