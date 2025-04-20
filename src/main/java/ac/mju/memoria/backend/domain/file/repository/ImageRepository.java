package ac.mju.memoria.backend.domain.file.repository;

import ac.mju.memoria.backend.domain.file.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, String> {
}
