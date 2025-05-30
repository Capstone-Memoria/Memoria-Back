package ac.mju.memoria.backend.domain.ai.networking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BasicNode implements Node {
  private final String URL;
  private Boolean available = true;

  @Override
  public Boolean isAvailable() {
    return this.available;
  }

  @Override
  public void setAvailable(Boolean available) {
    this.available = available;
  }
}