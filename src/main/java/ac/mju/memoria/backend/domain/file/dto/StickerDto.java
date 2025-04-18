package ac.mju.memoria.backend.domain.file.dto;

import ac.mju.memoria.backend.domain.file.entity.Sticker;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public class StickerDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StickerCreateRequest {
        @Builder.Default
        private StickerType stickerType = StickerType.NONE;
        private int posX;
        private int posY;
        private int width;
        private int height;
        @NotNull(message = "이미지 파일은 필수입니다")
        private MultipartFile file;

        public Sticker toEntity() {
            String filename = file.getOriginalFilename();
            if (filename == null || filename.isBlank()) {
                throw new RestException(ErrorCode.FILE_NOT_FOUND);
            }

            return Sticker.builder()
                    .id(UUID.randomUUID().toString())
                    .stickerType(stickerType)
                    .fileName(filename)
                    .size(file.getSize())
                    .fileType(FileType.IMAGE)
                    .posX(posX)
                    .posY(posY)
                    .width(width)
                    .height(height)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StickerAddRequest {
        @NotBlank(message = "stickerId는 필수입니다")
        private String stickerId;
        private int posX;
        private int posY;
        private int width;
        private int height;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StickerResponse {
        private String id;
        private StickerType stickerType;
        private String fileName;
        private Long size;
        private String fileType;
        private int posX;
        private int posY;
        private int width;
        private int height;

        public static StickerResponse from(Sticker sticker) {
            return StickerResponse.builder()
                    .id(sticker.getId())
                    .stickerType(sticker.getStickerType())
                    .fileName(sticker.getFileName())
                    .size(sticker.getSize())
                    .fileType(sticker.getFileType().toString())
                    .posX(sticker.getPosX())
                    .posY(sticker.getPosY())
                    .width(sticker.getWidth())
                    .height(sticker.getHeight())
                    .build();
        }
    }
}