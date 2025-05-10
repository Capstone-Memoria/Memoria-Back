package ac.mju.memoria.backend.domain.ai.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MusicJobResponse {
  private String jobId;
  private MusicJobStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime completedAt;
  private String filePath;
  private String error;
}