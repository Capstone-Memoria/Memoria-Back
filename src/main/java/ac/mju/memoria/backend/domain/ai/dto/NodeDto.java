package ac.mju.memoria.backend.domain.ai.dto;

import ac.mju.memoria.backend.domain.ai.entity.AiNode;
import ac.mju.memoria.backend.domain.ai.entity.NodeType;
import ac.mju.memoria.backend.domain.ai.networking.DBNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "AI 노드 정보 DTO")
public class NodeDto {

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(description = "AI 노드 생성 요청 DTO")
  @Builder
  public static class CreateRequest {
    @NotBlank(message = "노드 URL은 필수입니다")
    @Pattern(regexp = "^https?://.*", message = "올바른 URL 형식이어야 합니다")
    @Schema(description = "노드 URL", example = "http://localhost:8001")
    private String url;

    @Schema(description = "노드 타입", example = "IMAGE")
    private NodeType type;
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(description = "AI 노드 수정 요청 DTO")
  @Builder
  public static class UpdateRequest {
    @Pattern(regexp = "^https?://.*", message = "올바른 URL 형식이어야 합니다")
    @Schema(description = "노드 URL", example = "http://localhost:8001")
    private String url;
    @Schema(description = "노드 타입", example = "IMAGE")
    private NodeType type;
  }

  @Getter
  @AllArgsConstructor
  @NoArgsConstructor
  @Schema(description = "AI 노드 정보 응답 DTO")
  @Builder
  public static class Response {
    @Schema(description = "노드 ID", example = "1")
    private Long id;
    @Schema(description = "노드 타입", example = "IMAGE, MUSIC")
    private NodeType type;
    @Schema(description = "노드 URL", example = "http://localhost:8001")
    private String url;
    @Schema(description = "노드 사용 가능 여부", example = "true")
    private Boolean available;

    public static Response from(AiNode node, Boolean available) {
      return Response.builder()
          .id(node.getId())
          .type(node.getNodeType())
          .url(node.getUrl())
          .available(available)
          .build();
    }

    public static Response from(DBNode node, NodeType type) {
      return Response.builder()
          .id(node.getId())
          .type(type)
          .url(node.getURL())
          .available(node.isAvailable())
          .build();
    }
  }
}
