package ac.mju.memoria.backend.domain.diarybook.dto;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import ac.mju.memoria.backend.domain.diarybook.entity.stickers.AbstractSticker;
import ac.mju.memoria.backend.domain.diarybook.entity.stickers.CustomImageSticker;
import ac.mju.memoria.backend.domain.diarybook.entity.stickers.CustomTextSticker;
import ac.mju.memoria.backend.domain.diarybook.entity.stickers.PredefinedSticker;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public class StickerDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Schema(description = "스티커 수정 요청 DTO")
    public static class UpdateRequest {
        @Schema(description = "수정할 스티커 목록")
        private List<AbstractRequest> stickers;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = PredefinedStickerRequest.class, name = "PREDEFINED"),
            @JsonSubTypes.Type(value = CustomImageStickerRequest.class, name = "CUSTOM_IMAGE"),
            @JsonSubTypes.Type(value = CustomTextStickerRequest.class, name = "CUSTOM_TEXT")
    })
    @Schema(description = "추상 스티커 요청 DTO")
    public static abstract class AbstractRequest {
        @Schema(description = "스티커 UUID", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        private String uuid;
        private StickerType type;
        @Schema(description = "스티커 X 좌표", example = "0.5")
        private Double posX;
        @Schema(description = "스티커 Y 좌표", example = "0.5")
        private Double posY;
        @Schema(description = "스티커 크기", example = "1.0")
        private Double size;
        @Schema(description = "스티커 회전 각도", example = "0")
        private Integer rotation;

        public abstract AbstractSticker toEntity();
    }

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Schema(description = "기본 스티커 요청 DTO")
    public static class PredefinedStickerRequest extends AbstractRequest {
        @Schema(description = "에셋 이름", example = "heart_sticker")
        private String assetName;

        @Override
        @Schema(description = "스티커 타입", example = "PREDEFINED")
        public StickerType getType() {
            return super.getType();
        }

        @Override
        public AbstractSticker toEntity() {
            return PredefinedSticker.builder()
                    .uuid(UUID.randomUUID().toString())
                    .type(StickerType.PREDEFINED)
                    .posX(getPosX())
                    .posY(getPosY())
                    .size(getSize())
                    .rotation(getRotation())
                    .assetName(assetName)
                    .build();
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Schema(description = "커스텀 이미지 스티커 요청 DTO")
    public static class CustomImageStickerRequest extends AbstractRequest {
        @Schema(description = "보관된 스티커 이미지 UUID", example = "b1c2d3e4-f5g6-7890-1234-567890abcdef")
        private String heldStickerImageUuid;

        @Override
        @Schema(description = "스티커 타입", example = "CUSTOM_IMAGE")
        public StickerType getType() {
            return super.getType();
        }

        @Override
        public AbstractSticker toEntity() {
            return CustomImageSticker.builder()
                    .uuid(UUID.randomUUID().toString())
                    .type(StickerType.CUSTOM_IMAGE)
                    .posX(getPosX())
                    .posY(getPosY())
                    .size(getSize())
                    .rotation(getRotation())
                    .build();
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Schema(description = "커스텀 텍스트 스티커 요청 DTO")
    public static class CustomTextStickerRequest extends AbstractRequest {
        @Schema(description = "텍스트 내용", example = "Hello World!")
        private String textContent;
        @Schema(description = "글꼴 크기", example = "16")
        private Integer fontSize;
        @Schema(description = "글꼴 색상", example = "#000000")
        private String fontColor;

        @Override
        @Schema(description = "스티커 타입", example = "CUSTOM_TEXT")
        public StickerType getType() {
            return super.getType();
        }

        @Override
        public AbstractSticker toEntity() {
            return CustomTextSticker.builder()
                    .uuid(UUID.randomUUID().toString())
                    .type(StickerType.CUSTOM_TEXT)
                    .posX(getPosX())
                    .posY(getPosY())
                    .size(getSize())
                    .rotation(getRotation())
                    .textContent(textContent)
                    .fontSize(fontSize)
                    .fontColor(fontColor)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @Schema(description = "스티커 이미지 보관 요청 DTO")
    public static class HoldStickerImageRequest {
        @Schema(description = "이미지 파일")
        private MultipartFile imageFile;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @Schema(description = "스티커 이미지 보관 응답 DTO")
    public static class HoldStickerImageResponse {
        @Schema(description = "보관된 스티커 이미지 UUID", example = "c1d2e3f4-g5h6-7890-1234-567890abcdef")
        private String uuid;

        public static HoldStickerImageResponse from(String uuid) {
            return HoldStickerImageResponse.builder()
                    .uuid(uuid)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @SuperBuilder
    @Schema(description = "추상 스티커 응답 DTO")
    public static abstract class AbstractResponse {
        @Schema(description = "스티커 UUID", example = "d1e2f3g4-h5i6-7890-1234-567890abcdef")
        private String uuid;
        private StickerType type;
        @Schema(description = "다이어리 북 ID", example = "1")
        private Long diaryBookId;
        @Schema(description = "스티커 X 좌표", example = "0.5")
        private Double posX;
        @Schema(description = "스티커 Y 좌표", example = "0.5")
        private Double posY;
        @Schema(description = "스티커 크기", example = "1.0")
        private Double size;
        @Schema(description = "스티커 회전 각도", example = "0")
        private Integer rotation;

        public static AbstractResponse from(AbstractSticker sticker) {
            if (sticker instanceof PredefinedSticker) {
                return PredefinedStickerResponse.from((PredefinedSticker) sticker);
            } else if (sticker instanceof CustomImageSticker) {
                return CustomImageStickerResponse.from((CustomImageSticker) sticker);
            } else if (sticker instanceof CustomTextSticker) {
                return CustomTextStickerResponse.from((CustomTextSticker) sticker);
            }
            throw new IllegalArgumentException("Unknown sticker type: " + sticker.getClass().getSimpleName());
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @SuperBuilder
    @Schema(description = "기본 스티커 응답 DTO")
    public static class PredefinedStickerResponse extends AbstractResponse {
        @Schema(description = "에셋 이름", example = "heart_sticker")
        private String assetName;

        @Schema(description = "스티커 타입", example = "PREDEFINED")
        @Override
        public StickerType getType() {
            return super.getType();
        }

        public static PredefinedStickerResponse from(PredefinedSticker sticker) {
            return PredefinedStickerResponse.builder()
                    .uuid(sticker.getUuid())
                    .type(sticker.getType())
                    .diaryBookId(sticker.getDiaryBook().getId())
                    .posX(sticker.getPosX())
                    .posY(sticker.getPosY())
                    .size(sticker.getSize())
                    .rotation(sticker.getRotation())
                    .assetName(sticker.getAssetName())
                    .build();
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @SuperBuilder
    @Schema(description = "커스텀 이미지 스티커 응답 DTO")
    public static class CustomImageStickerResponse extends AbstractResponse {
        @Schema(description = "이미지 파일 정보")
        private FileDto.FileResponse imageFile;

        @Schema(description = "스티커 타입", example = "CUSTOM_IMAGE")
        @Override
        public StickerType getType() {
            return super.getType();
        }

        public static CustomImageStickerResponse from(CustomImageSticker sticker) {
            return CustomImageStickerResponse.builder()
                    .uuid(sticker.getUuid())
                    .type(sticker.getType())
                    .diaryBookId(sticker.getDiaryBook().getId())
                    .posX(sticker.getPosX())
                    .posY(sticker.getPosY())
                    .size(sticker.getSize())
                    .rotation(sticker.getRotation())
                    .imageFile(FileDto.FileResponse.from(sticker.getImageFile()))
                    .build();
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @SuperBuilder
    @Schema(description = "커스텀 텍스트 스티커 응답 DTO")
    public static class CustomTextStickerResponse extends AbstractResponse {
        @Schema(description = "텍스트 내용", example = "Hello World!")
        private String textContent;
        @Schema(description = "글꼴 크기", example = "16")
        private Integer fontSize;
        @Schema(description = "글꼴 색상", example = "#000000")
        private String fontColor;

        @Schema(description = "스티커 타입", example = "CUSTOM_TEXT")
        @Override
        public StickerType getType() {
            return super.getType();
        }

        public static CustomTextStickerResponse from(CustomTextSticker sticker) {
            return CustomTextStickerResponse.builder()
                    .uuid(sticker.getUuid())
                    .type(sticker.getType())
                    .diaryBookId(sticker.getDiaryBook().getId())
                    .posX(sticker.getPosX())
                    .posY(sticker.getPosY())
                    .size(sticker.getSize())
                    .rotation(sticker.getRotation())
                    .textContent(sticker.getTextContent())
                    .fontSize(sticker.getFontSize())
                    .fontColor(sticker.getFontColor())
                    .build();
        }
    }
}