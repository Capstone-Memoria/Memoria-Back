package ac.mju.memoria.backend.system.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "ai.music")
@Data
public class AiConfigProperties {

  private List<MusicServerNodeProperties> nodes = new ArrayList<>();

  @Data
  public static class MusicServerNodeProperties {
    private String host;
    private String port;
  }
}