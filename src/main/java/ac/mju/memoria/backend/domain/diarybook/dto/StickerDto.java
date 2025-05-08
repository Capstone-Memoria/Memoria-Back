package ac.mju.memoria.backend.domain.diarybook.dto;

import java.util.ArrayList;
import java.util.List;

import ac.mju.memoria.backend.domain.diarybook.entity.Sticker;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    public static class StickerInfo {
        @NotNull(message = "stickerType은 필수입니다")
        @Schema(description = "스티커 타입", example = "BEAR")
        private StickerType stickerType;
        @Schema(description = "스티커 X 좌표", example = "100")
        private int posX;
        @Schema(description = "스티커 Y 좌표", example = "150")
        private int posY;
        @Schema(description = "스티커 너비", example = "50")
        private int width;
        @Schema(description = "스티커 높이", example = "50")
        private int height;

        public Sticker toEntity() {
            return Sticker.builder()
                    .stickerType(stickerType)
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
    @Schema(description = "다이어리 북 스티커 업데이트 요청 DTO")
    public static class StickerUpdateRequest {
        @Builder.Default
        @Schema(description = "업데이트할 스티커 정보 목록")
        private List<StickerInfo> stickers = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "다이어리 북 스티커 응답 DTO")
    public static class StickerResponse {
        @Schema(description = "스티커 ID")
        private Long id;
        @Schema(description = "스티커 타입")
        private StickerType stickerType;
        @Schema(description = "스티커가 부착된 다이어리 북 ID")
        private Long diaryBookId; // 순환 참조 방지를 위해 ID만 반환
        @Schema(description = "스티커 X 좌표")
        private int posX;
        @Schema(description = "스티커 Y 좌표")
        private int posY;
        @Schema(description = "스티커 너비")
        private int width;
        @Schema(description = "스티커 높이")
        private int height;

        public static StickerResponse from(Sticker sticker) {
            return StickerResponse.builder()
                    .id(sticker.getId())
                    .stickerType(sticker.getStickerType())
                    .diaryBookId(sticker.getDiaryBook().getId()) // ID만 설정
                    .posX(sticker.getPosX())
                    .posY(sticker.getPosY())
                    .width(sticker.getWidth())
                    .height(sticker.getHeight())
                    .build();
        }
    }
}