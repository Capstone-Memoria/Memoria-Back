package ac.mju.memoria.backend.domain.ai.networking.image;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ac.mju.memoria.backend.domain.ai.dto.ImageDto;
import ac.mju.memoria.backend.domain.ai.networking.AbstractSyncNodePool;
import ac.mju.memoria.backend.domain.ai.networking.DBNode;
import ac.mju.memoria.backend.domain.ai.networking.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 이미지 생성을 위한 동기 방식의 NodePool입니다.
 * {@link ac.mju.memoria.backend.domain.ai.networking.AbstractSyncNodePool}을
 * 상속받아 이미지 생성 요청을 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageNodePool extends AbstractSyncNodePool<ImageDto.InternalCreateRequest, String> {
    private final OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public Optional<DBNode> getNodeById(Long id) {
        return getNodes().stream()
                .filter(node -> ((DBNode) node).getId().equals(id))
                .findFirst()
                .map(DBNode.class::cast);
    }

    @PostConstruct
    @Override
    public void start() {
        super.start();
    }

    /**
     * 주어진 요청과 노드를 사용하여 이미지 생성 요청을 동기적으로 처리합니다.
     * HTTP POST 요청을 생성하여 이미지 생성 서버의 "/generate" 엔드포인트로 전송합니다.
     * 응답으로 받은 JSON에서 이미지 URL을 추출하여 반환합니다.
     *
     * @param request 이미지 생성에 필요한 정보를 담고 있는 {@link ImageDto.InternalCreateRequest}
     *                객체
     * @param node    요청을 처리할 {@link Node} 객체
     * @return 생성된 이미지의 URL 문자열
     * @throws RuntimeException 이미지 생성 요청이 실패하거나 응답 처리 중 오류가 발생한 경우
     */
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
