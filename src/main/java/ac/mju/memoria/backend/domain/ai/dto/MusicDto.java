package ac.mju.memoria.backend.domain.ai.dto;

import ac.mju.memoria.backend.domain.ai.model.MusicJobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class CreateResponse {
        private String jobId;
        private MusicJobStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
        private String filePath;
        private String error;
    }
}
