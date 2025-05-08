package ac.mju.memoria.backend.domain.ai.sse;

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
public class SseResponse {
    private String activeJob;
    private Map<String, JobInfo> jobs;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class JobInfo {
        private String status; //processing, completed
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
        @Nullable
        private String filePath;
        @Nullable
        private String error;
    }
}
