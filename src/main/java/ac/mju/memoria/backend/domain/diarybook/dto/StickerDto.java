package ac.mju.memoria.backend.domain.diarybook.dto;

import ac.mju.memoria.backend.domain.diarybook.entity.CustomImageSticker;
import ac.mju.memoria.backend.domain.diarybook.entity.CustomTextSticker;
import ac.mju.memoria.backend.domain.diarybook.entity.PredefinedSticker;
import ac.mju.memoria.backend.domain.diarybook.entity.Sticker;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StickerDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Schema(description = "스티커 공통 요청 정보")
    public static abstract class BaseStickerRequest {
        @NotNull(message = "X 좌표는 필수입니다.")
        @Schema(description = "스티커 X 좌표")
        private Double posX;

        @NotNull(message = "Y 좌표는 필수입니다.")
        @Schema(description = "스티커 Y 좌표")
        private Double posY;

        @Schema(description = "스티커 크기")
        private Double size;

        @Schema(description = "스티커 회전 각도 (0-359)")
        @Min(value = 0, message = "회전 각도는 0 이상이어야 합니다.")
        @Max(value = 359, message = "회전 각도는 359 이하이어야 합니다.")
        private Integer rotation;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Schema(description = "사전 정의된 스티커 요청 DTO")
    public static class PredefinedStickerRequest extends BaseStickerRequest {
        @NotBlank(message = "스티커 UUID는 필수입니다.")
        @Schema(description = "스티커의 고유 식별자")
        private String uuid;

        @NotBlank(message = "에셋 이름은 필수입니다.")
        @Schema(description = "사전 정의된 스티커의 에셋 이름")
        private String assetName;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Schema(description = "커스텀 이미지 스티커 생성 요청 DTO")
    public static class CustomImageStickerCreateRequest extends BaseStickerRequest {
        @Schema(description = "업로드할 이미지 파일")
        private MultipartFile imageFile;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Schema(description = "커스텀 텍스트 스티커 요청 DTO")
    public static class CustomTextStickerRequest extends BaseStickerRequest {
        @NotBlank(message = "텍스트 내용은 필수입니다.")
        @Schema(description = "스티커에 표시될 텍스트 내용")
        private String textContent;

        @Schema(description = "폰트 패밀리")
        private String fontFamily;

        @Schema(description = "폰트 크기")
        private String fontSize;

        @Schema(description = "폰트 색상")
        private String fontColor;

        @Schema(description = "배경 색상")
        private String backgroundColor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Schema(description = "다이어리 북 스티커 전체 업데이트 요청 DTO")
    public static class StickerUpdateRequest {
        @Builder.Default
        @Valid
        @Schema(description = "업데이트할 사전 정의된 스티커 정보 목록")
        private List<PredefinedStickerRequest> predefinedStickers = new ArrayList<>();

        @Builder.Default
        @Valid
        @Schema(description = "업데이트할 커스텀 이미지 스티커 정보 목록 (생성 시 이미지 파일 포함)")
        private List<CustomImageStickerCreateRequest> customImageStickers = new ArrayList<>();

        @Builder.Default
        @Valid
        @Schema(description = "업데이트할 커스텀 텍스트 스티커 정보 목록")
        private List<CustomTextStickerRequest> customTextStickers = new ArrayList<>();
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Schema(description = "스티커 공통 응답 정보")
    public static abstract class BaseStickerResponse {
        @Schema(description = "스티커 UUID")
        private String uuid;

        @Schema(description = "스티커 타입")
        private StickerType stickerType;

        @Schema(description = "스티커가 부착된 다이어리 북 ID")
        private Long diaryBookId;

        @Schema(description = "스티커 X 좌표")
        private Double posX;

        @Schema(description = "스티커 Y 좌표")
        private Double posY;

        @Schema(description = "스티커 크기")
        private Double size;

        @Schema(description = "스티커 회전 각도")
        private Integer rotation;

        @Schema(description = "스티커 생성자 정보")
        private UserDto.UserResponse createdBy;

        @Schema(description = "스티커 생성 시간")
        private LocalDateTime createdAt;

        @Schema(description = "스티커 마지막 수정 시간")
        private LocalDateTime lastModifiedAt;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Schema(description = "사전 정의된 스티커 응답 DTO")
    public static class PredefinedStickerResponse extends BaseStickerResponse {
        @Schema(description = "사전 정의된 스티커의 에셋 이름")
        private String assetName;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Schema(description = "커스텀 이미지 스티커 응답 DTO")
    public static class CustomImageStickerResponse extends BaseStickerResponse {
        @Schema(description = "업로드된 이미지 파일 정보")
        private FileDto.FileResponse imageFile;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Schema(description = "커스텀 텍스트 스티커 응답 DTO")
    public static class CustomTextStickerResponse extends BaseStickerResponse {
        @Schema(description = "스티커 텍스트 내용")
        private String textContent;
        @Schema(description = "폰트 패밀리")
        private String fontFamily;
        @Schema(description = "폰트 크기")
        private String fontSize;
        @Schema(description = "폰트 색상")
        private String fontColor;
        @Schema(description = "배경 색상")
        private String backgroundColor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @SuperBuilder
    @Schema(description = "다이어리 북의 모든 스티커 목록 응답 DTO")
    public static class AllStickersResponse {
        @Builder.Default
        @Schema(description = "사전 정의된 스티커 목록")
        private List<PredefinedStickerResponse> predefinedStickers = new ArrayList<>();
        @Builder.Default
        @Schema(description = "커스텀 이미지 스티커 목록")
        private List<CustomImageStickerResponse> customImageStickers = new ArrayList<>();
        @Builder.Default
        @Schema(description = "커스텀 텍스트 스티커 목록")
        private List<CustomTextStickerResponse> customTextStickers = new ArrayList<>();
    }
    public static void toAllStickerResponse(AllStickersResponse allStickersResponse, Sticker sticker) {
        if (sticker.getStickerType() == StickerType.PREDEFINED) {
            allStickersResponse.getPredefinedStickers().add(StickerDto.toPredefinedResponse((PredefinedSticker) sticker));
        } else if (sticker.getStickerType() == StickerType.CUSTOM_IMAGE) {
            allStickersResponse.getCustomImageStickers().add(StickerDto.toCustomImageResponse((CustomImageSticker) sticker));
        } else if (sticker.getStickerType() == StickerType.CUSTOM_TEXT) {
            allStickersResponse.getCustomTextStickers().add(StickerDto.toCustomTextResponse((CustomTextSticker) sticker));
        }
    }

    public static PredefinedStickerResponse toPredefinedResponse(PredefinedSticker entity) {
        if (entity == null) return null;
        return PredefinedStickerResponse.builder()
                .uuid(entity.getUuid())
                .stickerType(entity.getStickerType())
                .diaryBookId(entity.getDiaryBook().getId())
                .posX(entity.getPosX())
                .posY(entity.getPosY())
                .size(entity.getSize())
                .rotation(entity.getRotation())
                .createdBy(UserDto.UserResponse.from(entity.getCreatedBy()))
                .createdAt(entity.getCreatedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .assetName(entity.getAssetName())
                .build();
    }

    public static CustomImageStickerResponse toCustomImageResponse(CustomImageSticker entity) {
        if (entity == null) return null;
        return CustomImageStickerResponse.builder()
                .uuid(entity.getUuid())
                .stickerType(entity.getStickerType())
                .diaryBookId(entity.getDiaryBook().getId())
                .posX(entity.getPosX())
                .posY(entity.getPosY())
                .size(entity.getSize())
                .rotation(entity.getRotation())
                .createdBy(UserDto.UserResponse.from(entity.getCreatedBy()))
                .createdAt(entity.getCreatedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .imageFile(entity.getImageFile() != null ? FileDto.FileResponse.from(entity.getImageFile()) : null)
                .build();
    }

    public static CustomTextStickerResponse toCustomTextResponse(CustomTextSticker entity) {
        if (entity == null) return null;
        return CustomTextStickerResponse.builder()
                .uuid(entity.getUuid())
                .stickerType(entity.getStickerType())
                .diaryBookId(entity.getDiaryBook().getId())
                .posX(entity.getPosX())
                .posY(entity.getPosY())
                .size(entity.getSize())
                .rotation(entity.getRotation())
                .createdBy(UserDto.UserResponse.from(entity.getCreatedBy()))
                .createdAt(entity.getCreatedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .textContent(entity.getTextContent())
                .fontFamily(entity.getFontFamily())
                .fontSize(entity.getFontSize())
                .fontColor(entity.getFontColor())
                .backgroundColor(entity.getBackgroundColor())
                .build();
    }
}