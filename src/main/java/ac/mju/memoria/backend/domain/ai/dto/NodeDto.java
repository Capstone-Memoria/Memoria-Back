package ac.mju.memoria.backend.domain.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "AI 노드 정보 DTO")
public class NodeDto {

  @Getter
  @NoArgsConstructor
  @Schema(description = "AI 노드 생성 요청 DTO")
  public static class CreateRequest {
    @Schema(description = "노드 URL", example = "http://localhost:8001")
    private String url;

    @Builder
    public CreateRequest(String url) {
      this.url = url;
    }
  }

  @Getter
  @Schema(description = "AI 노드 정보 응답 DTO")
  public static class InfoResponse {
    @Schema(description = "노드 URL", example = "http://localhost:8001")
    private final String url;
    @Schema(description = "노드 사용 가능 여부", example = "true")
    private final Boolean available;

    @Builder
    public InfoResponse(String url, Boolean available) {
      this.url = url;
      this.available = available;
    }
  }
}