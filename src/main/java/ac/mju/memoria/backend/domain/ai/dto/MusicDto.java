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
    public static class CreateRequest {
        private String genre;
        private String lyrics;
    }

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
    public static class CreateRequestSync {
        private String genre_txt;
        private String lyrics_txt;
    }
}
