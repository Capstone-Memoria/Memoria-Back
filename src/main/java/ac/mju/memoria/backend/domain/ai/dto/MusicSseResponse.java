package ac.mju.memoria.backend.domain.ai.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MusicSseResponse {
    @JsonAlias("active_job")
    private String activeJob;
    private Map<String, JobInfo> jobs;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class JobInfo {
        private String status; //processing, completed
        @JsonAlias("created_at")
        private LocalDateTime createdAt;
        @JsonAlias("completed_at")
        private LocalDateTime completedAt;
        @Nullable
        @JsonAlias("file_path")
        private String filePath;
        @Nullable
        private String error;
    }
}
