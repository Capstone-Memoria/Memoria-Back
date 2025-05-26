package ac.mju.memoria.backend.system.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfigProperties {

  private List<NodeProperties> musicNodes = new ArrayList<>();
  private List<NodeProperties> imageNodes = new ArrayList<>();

  @Data
  public static class NodeProperties {
    private String url;
  }
}