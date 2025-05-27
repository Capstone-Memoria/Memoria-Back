package ac.mju.memoria.backend.domain.ai.service;

import ac.mju.memoria.backend.domain.ai.dto.ImageDto;
import ac.mju.memoria.backend.domain.ai.llm.model.StableDiffusionPrompts;
import ac.mju.memoria.backend.domain.ai.llm.service.PromptGenerator;
import ac.mju.memoria.backend.domain.ai.networking.DefaultNode;
import ac.mju.memoria.backend.domain.ai.networking.image.ImageNodePool;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

@Slf4j
@Service
public class ImageCreateService {
    private final GoogleAiGeminiChatModel chatModel;
    private final ImageNodePool imageNodePool = new ImageNodePool();

    public ImageCreateService(
        GoogleAiGeminiChatModel chatModel,
        @Qualifier("imageServerNodes")
        List<DefaultNode> imageServerNodes
    ) {
        this.chatModel = chatModel;
        imageServerNodes.forEach(imageNodePool::addNode);
    }

    @PostConstruct
    private void init() {
        imageNodePool.start();
    }


    @SneakyThrows
    public String generateImage(ImageDto.CreateRequest request) {
        ImageDto.InternalCreateRequest req = buildChatRequest(
            request.getDescription()
        );

        Future<String> response = imageNodePool.submitRequest(req);
        return response.get();
    }

    @SneakyThrows
    private ImageDto.InternalCreateRequest buildChatRequest(
        String description
    ) {
        PromptGenerator promptGenerator = AiServices.builder(
            PromptGenerator.class
        )
            .chatLanguageModel(chatModel)
            .build();

        StableDiffusionPrompts result = promptGenerator.generatePrompts(
            description
        );

        return ImageDto.InternalCreateRequest.builder()
            .prompt(result.prompt())
            .negativePrompt(result.negative_prompt())
            .build();
    }
}
