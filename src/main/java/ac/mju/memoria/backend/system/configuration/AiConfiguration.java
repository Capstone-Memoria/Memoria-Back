package ac.mju.memoria.backend.system.configuration;

import java.util.List;
import java.util.stream.Collectors;

import ac.mju.memoria.backend.domain.ai.llm.service.MusicPromptGenerator;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ac.mju.memoria.backend.domain.ai.model.MusicServerNode;

@Configuration
@RequiredArgsConstructor
public class AiConfiguration {
    private final GoogleAiGeminiChatModel chatModel;

    @Bean
    public List<MusicServerNode> musicServerNodes(AiConfigProperties aiConfigProperties) {
        return aiConfigProperties.getNodes().stream()
                .map(nodeProperties ->
                        new MusicServerNode(
                                nodeProperties.getHost()
                ))
                .collect(Collectors.toList());
    }

    @Bean
    public MusicPromptGenerator musicPromptGenerator() {
        return AiServices.builder(MusicPromptGenerator.class)
                .chatLanguageModel(chatModel)
                .build();
    }
}