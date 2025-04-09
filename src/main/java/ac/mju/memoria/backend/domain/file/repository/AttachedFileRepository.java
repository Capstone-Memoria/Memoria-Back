package ac.mju.memoria.backend.domain.file.repository;

import ac.mju.memoria.backend.domain.file.entity.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachedFileRepository extends JpaRepository<AttachedFile, String> {
}
