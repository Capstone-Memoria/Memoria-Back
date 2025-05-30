package ac.mju.memoria.backend.system.configuration;

import ac.mju.memoria.backend.domain.ai.llm.service.MusicPromptGenerator;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class AiConfiguration {
    private final GoogleAiGeminiChatModel chatModel;

    @Bean
    public MusicPromptGenerator musicPromptGenerator() {
        return AiServices.builder(MusicPromptGenerator.class)
                .chatLanguageModel(chatModel)
                .build();
    }
}