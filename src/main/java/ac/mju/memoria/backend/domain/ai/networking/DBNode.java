package ac.mju.memoria.backend.domain.ai.networking;

import ac.mju.memoria.backend.domain.ai.entity.AiNode;
import lombok.*;

import java.util.Objects;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class DBNode implements Node {
  private Long id;
  private String URL;
  @Builder.Default
  private Boolean available = true;

  @Override
  public Boolean isAvailable() {
    return this.available;
  }

  @Override
  public void setAvailable(Boolean available) {
    this.available = available;
  }

  public static DBNode from(AiNode node) {
    return DBNode.builder()
        .id(node.getId())
        .URL(node.getUrl())
        .available(true)
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DBNode dbNode = (DBNode) o;
    return Objects.equals(id, dbNode.id) && Objects.equals(URL, dbNode.URL);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, URL);
  }
}