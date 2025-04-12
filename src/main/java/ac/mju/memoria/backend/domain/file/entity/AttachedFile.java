package ac.mju.memoria.backend.domain.file.entity;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public abstract class AttachedFile extends UserStampedEntity {
    @Id @Setter(AccessLevel.NONE)
    private String id;

    private String fileName;

    private Long size;

    @Enumerated(EnumType.STRING)
    private FileType fileType;
}
