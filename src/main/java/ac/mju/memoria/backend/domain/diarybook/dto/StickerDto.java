package ac.mju.memoria.backend.domain.diarybook.dto;

import java.util.ArrayList;
import java.util.List;

import ac.mju.memoria.backend.domain.diarybook.entity.Sticker;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StickerDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "스티커 정보 DTO")
    public static class StickerRequest {
        @NotNull(message = "다이어리 북 UUID는 필수입니다")
        @Schema(description = "다이어리 북 UUID", example = "uuid")
        private String uuid;

        @NotNull(message = "stickerType은 필수입니다")
        @Schema(description = "스티커 타입", example = "BEAR")
        private String stickerType;

        @Schema(description = "스티커 X 좌표", example = "100")
        private Integer posX;

        @Schema(description = "스티커 Y 좌표", example = "150")
        private Integer posY;

        @Schema(description = "스티커 크기", example = "1")
        private Double scale;

        @Schema(description = "스티커 회전 각도", example = "0")
        @Max(value = 360, message = "회전 각도는 0에서 360 사이여야 합니다")
        @Min(value = 0, message = "회전 각도는 0에서 360 사이여야 합니다")
        private int rotation;

        public Sticker toEntity() {
            return Sticker.builder()
                    .uuid(uuid)
                    .stickerType(stickerType)
                    .posX(posX)
                    .posY(posY)
                    .scale(scale)
                    .rotation(rotation)
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "다이어리 북 스티커 업데이트 요청 DTO")
    public static class StickerUpdateRequest {
        @Builder.Default
        @Schema(description = "업데이트할 스티커 정보 목록")
        private List<StickerRequest> stickers = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "다이어리 북 스티커 응답 DTO")
    public static class StickerResponse {
        @Schema(description = "스티커 ID")
        private String uuid;
        @Schema(description = "스티커 타입")
        private String stickerType;
        @Schema(description = "스티커가 부착된 다이어리 북 ID")
        private Long diaryBookId; // 순환 참조 방지를 위해 ID만 반환
        @Schema(description = "스티커 X 좌표")
        private Integer posX;
        @Schema(description = "스티커 Y 좌표")
        private Integer posY;
        @Schema(description = "스티커 크기")
        private Double scale;
        @Schema(description = "스티커 회전 각도")
        private Integer rotation;

        public static StickerResponse from(Sticker sticker) {
            return StickerResponse.builder()
                    .uuid(sticker.getUuid())
                    .stickerType(sticker.getStickerType())
                    .diaryBookId(sticker.getDiaryBook().getId()) // ID만 설정
                    .posX(sticker.getPosX())
                    .posY(sticker.getPosY())
                    .scale(sticker.getScale())
                    .rotation(sticker.getRotation())
                    .build();
        }
    }
}