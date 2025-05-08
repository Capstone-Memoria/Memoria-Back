package ac.mju.memoria.backend.system.configuration;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ac.mju.memoria.backend.domain.ai.model.MusicServerNode;

@Configuration
public class AiConfiguration {

    @Bean
    public List<MusicServerNode> musicServerNodes(AiConfigProperties aiConfigProperties) {
        return aiConfigProperties.getNodes().stream()
                .map(nodeProperties ->
                        new MusicServerNode(
                                nodeProperties.getHost()
                ))
                .collect(Collectors.toList());
    }
}