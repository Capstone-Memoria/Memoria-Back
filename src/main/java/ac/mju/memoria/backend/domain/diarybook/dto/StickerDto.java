package ac.mju.memoria.backend.domain.diarybook.dto;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.Sticker;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class StickerDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StickerInfo {
        @NotBlank(message = "stickerType은 필수입니다")
        private StickerType stickerType;
        private int posX;
        private int posY;
        private int width;
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
    public static class StickerUpdateRequest {
        @Builder.Default
        private List<StickerInfo> stickers = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StickerResponse {
        private Long id;
        private StickerType stickerType;
        private DiaryBook diaryBook;
        private int posX;
        private int posY;
        private int width;
        private int height;

        public static StickerResponse from(Sticker sticker) {
            return StickerResponse.builder()
                    .id(sticker.getId())
                    .stickerType(sticker.getStickerType())
                    .diaryBook(sticker.getDiaryBook())
                    .posX(sticker.getPosX())
                    .posY(sticker.getPosY())
                    .width(sticker.getWidth())
                    .height(sticker.getHeight())
                    .build();
        }
    }
}