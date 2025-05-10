package ac.mju.memoria.backend.domain.file.entity;

import ac.mju.memoria.backend.domain.diarybook.entity.AICharacter;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ProfileImage extends AttachedFile{
    @OneToOne
    private AICharacter aiCharacter;

    public static ProfileImage from(MultipartFile file) {
        throwIfNotAImageFile(file);

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new RestException(ErrorCode.FILE_NOT_FOUND);
        }

        return ProfileImage.builder()
                .id(UUID.randomUUID().toString())
                .fileName(file.getOriginalFilename())
                .size(file.getSize())
                .fileType(FileType.IMAGE)
                .build();
    }

    private static void throwIfNotAImageFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String[] splitted = fileName.split("\\.");

        if(splitted.length < 1) {
            throw new RestException(ErrorCode.FILE_NOT_IMAGE);
        }

        String extension = splitted[splitted.length - 1];
        if(!List.of("PNG", "JPG", "JPEG", "GIF", "WEBP").contains(extension.toUpperCase())) {
            throw new RestException(ErrorCode.FILE_NOT_IMAGE);
        }
    }

}
