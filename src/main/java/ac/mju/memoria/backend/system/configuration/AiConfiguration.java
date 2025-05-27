package ac.mju.memoria.backend.system.configuration;

import java.util.List;
import java.util.stream.Collectors;

import ac.mju.memoria.backend.domain.ai.llm.service.MusicPromptGenerator;
import ac.mju.memoria.backend.domain.ai.networking.DefaultNode;
import ac.mju.memoria.backend.domain.ai.networking.Node;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class AiConfiguration {
    private final GoogleAiGeminiChatModel chatModel;

    @Bean
    @Qualifier("imageServerNodes")
    public List<DefaultNode> imageServerNodes(AiConfigProperties aiConfigProperties) {
        return aiConfigProperties.getImageNodes()
                .stream()
                .map(nodeProperties ->
                        DefaultNode.fromURL(nodeProperties.getUrl()))
                .collect(Collectors.toList());
    }

    @Bean
    @Qualifier("musicServerNodes")
    public List<DefaultNode> musicServerNodes(AiConfigProperties aiConfigProperties) {
        return aiConfigProperties.getMusicNodes().stream()
                .map(nodeProperties ->
                        DefaultNode.fromURL(nodeProperties.getUrl()))
                .collect(Collectors.toList());
    }

    @Bean
    public MusicPromptGenerator musicPromptGenerator() {
        return AiServices.builder(MusicPromptGenerator.class)
                .chatLanguageModel(chatModel)
                .build();
    }
}