package ac.mju.memoria.backend.domain.ai.networking.image;

import ac.mju.memoria.backend.domain.ai.dto.ImageDto;
import ac.mju.memoria.backend.domain.ai.networking.AbstrctSyncNodePool;
import ac.mju.memoria.backend.domain.ai.networking.Node;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Objects;

@Slf4j
public class ImageNodePool extends AbstrctSyncNodePool<ImageDto.InternalCreateRequest, String> {
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected String handleRequest(ImageDto.InternalCreateRequest request, Node node) {
        FormBody formBody = new FormBody.Builder()
                .add("prompt", request.getPrompt())
                .add("negative_prompt", request.getNegativePrompt())
                .build();

        Request httpRequest = new Request.Builder()
                .url(node.getURL() + "/generate")
                .post(formBody)
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("Image Creation Request failed: {}", response.code());
                throw new RuntimeException("Image generation failed: " + response.message());
            }

            String responseBody = Objects.requireNonNull(response.body()).string();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(responseBody);

            return jsonNode.get("image").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error during image generation: " + e.getMessage(), e);
        }
    }
}
