package ac.mju.memoria.backend.domain.ai.networking.music;

import ac.mju.memoria.backend.common.utils.JsonUtils;
import ac.mju.memoria.backend.domain.ai.dto.MusicDto;
import ac.mju.memoria.backend.domain.ai.networking.AbstractNode;
import ac.mju.memoria.backend.domain.ai.networking.ResponseHandler;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;


@Slf4j
public class MusicNode extends AbstractNode<MusicDto.CreateRequest> {
    public MusicNode(OkHttpClient client) {
        super(client);
    }

    @Override
    protected void sendRequest(MusicDto.CreateRequest request, ResponseHandler handler) {
        RequestBody requestBody = RequestBody.create(
                JsonUtils.toJson(request),
                MediaType.parse("application/json")
        );
        Request requestToSend = new Request.Builder()
                .url(getURL())
                .post(requestBody)
                .build();

        try (Response response = client.newCall(requestToSend).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to send request to music node: {}", response.body().string());
                this.isAvailable = false;
                return;
            }

            String body = Objects.requireNonNull(response.body()).string();
            log.info("Music node response: {}", body);
            handler.handleResponse(body);
        } catch (IOException | NullPointerException e) {
            log.error(e.getMessage(), e);
            this.isAvailable = true;
        }
    }
}