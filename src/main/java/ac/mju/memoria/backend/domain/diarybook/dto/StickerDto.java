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
    public static class StickerUpdateRequest {
        @Builder.Default
        private List<StickerAddRequest> stickers = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StickerResponse {
        private String id;
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