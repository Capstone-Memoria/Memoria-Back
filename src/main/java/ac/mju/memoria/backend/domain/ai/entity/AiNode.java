package ac.mju.memoria.backend.domain.ai.entity;

import ac.mju.memoria.backend.common.auditor.TimeStampedEntity;
import ac.mju.memoria.backend.domain.ai.dto.NodeDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Table(name = "ai_node")
public class AiNode extends TimeStampedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String url;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NodeType nodeType;

  public static AiNode from(NodeDto.CreateRequest request) {
    return AiNode.builder()
        .url(request.getUrl())
        .nodeType(request.getType())
        .build();
  }

  public void updateFromRequest(NodeDto.UpdateRequest request) {
    if (request.getUrl() != null) {
      this.url = request.getUrl();
    }
    if (request.getType() != null) {
      this.nodeType = request.getType();
    }
  }
}