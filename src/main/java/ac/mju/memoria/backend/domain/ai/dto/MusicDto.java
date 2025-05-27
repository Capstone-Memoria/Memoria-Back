package ac.mju.memoria.backend.domain.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MusicDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class StatusResponse {
        private boolean isGeneratingMusic;
        private boolean available;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class CreateRequest {
        private String genre_txt;
        private String lyrics_txt;

        public static CreateRequest from(String genre) {
            return CreateRequest.builder()
                    .genre_txt(genre)
                    .lyrics_txt("[chorus]\n\n\n\n\n\n[chorus]\n\n\n\n\n\n")
                    .build();
        }

        public static CreateRequest of(String genre, String lyrics) {
            return CreateRequest.builder()
                    .genre_txt(genre + "\n")
                    .lyrics_txt(lyrics + "\n")
                    .build();
        }
    }
}
