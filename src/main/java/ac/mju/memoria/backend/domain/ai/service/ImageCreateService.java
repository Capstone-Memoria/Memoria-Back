package ac.mju.memoria.backend.domain.ai.service;

import java.util.Map;
import java.util.Objects;

import ac.mju.memoria.backend.domain.ai.dto.ImageDto;
import ac.mju.memoria.backend.domain.ai.llm.model.StableDiffusionPrompts;
import ac.mju.memoria.backend.domain.ai.llm.service.PromptGenerator;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.output.JsonSchemas;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCreateService {
    private static final String API_URL = "https://83x3bdmyi1ovwt-8080.proxy.runpod.net/generate";
    private final GoogleAiGeminiChatModel chatModel;
    private final OkHttpClient client = new OkHttpClient();

    @SneakyThrows
    public String generateImage(ImageDto.CreateRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();

        ImageDto.InternalCreateRequest req = buildChatRequest(request.getDescription());

        FormBody formBody = new FormBody.Builder()
                .add("prompt", req.getPrompt())
                .add("negative_prompt", req.getNegativePrompt())
                .build();
        Request toSend = new Request.Builder()
                .url(API_URL)
                .post(formBody)
                .build();

        try (Response response = client.newCall(toSend).execute()) {
            if (!response.isSuccessful()) {
                throw new RestException(ErrorCode.AI_IMAGE_CREATION_FAILED);
            }

            String responseBody = Objects.requireNonNull(response.body()).string();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            return jsonNode.get("image").asText();
        } catch (Exception e) {
            throw new RestException(ErrorCode.AI_IMAGE_CREATION_FAILED);
        }
    }

    @SneakyThrows
    private ImageDto.InternalCreateRequest buildChatRequest(String description) {
        PromptGenerator promptGenerator = AiServices.builder(PromptGenerator.class)
                .chatLanguageModel(chatModel)
                .build();

        StableDiffusionPrompts result = promptGenerator.generatePrompts(description);

        return ImageDto.InternalCreateRequest.builder()
                .prompt(result.prompt())
                .negativePrompt(result.negative_prompt())
                .build();
    }
}
