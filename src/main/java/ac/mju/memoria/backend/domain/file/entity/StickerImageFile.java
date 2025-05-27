package ac.mju.memoria.backend.domain.file.entity;

import ac.mju.memoria.backend.domain.diarybook.entity.stickers.CustomImageSticker;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Entity
@NoArgsConstructor
@SuperBuilder
@Getter @Setter
public class StickerImageFile extends AttachedFile {
    @OneToOne
    private CustomImageSticker sticker;

    public static StickerImageFile from(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RestException(ErrorCode.GLOBAL_INVALID_PARAMETER);
        }

        return StickerImageFile.builder()
                .id(UUID.randomUUID().toString())
                .fileName(file.getOriginalFilename())
                .size(file.getSize())
                .fileType(FileType.IMAGE)
                .build();
    }

    public static StickerImageFile ofNew() {
        return StickerImageFile.builder()
                .id(UUID.randomUUID().toString())
                .fileName("default_sticker.png")
                .size(null) // Temporary file size, can be set later
                .fileType(FileType.IMAGE)
                .build();
    }
}
