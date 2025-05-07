package ac.mju.memoria.backend.domain.ai.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.ObjectMapper;

import ac.mju.memoria.backend.domain.ai.dto.MusicDto;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@RequiredArgsConstructor
@Data
public class MusicServerNode {
    private static final MediaType JSON = MediaType.get("application/json");
    private final String host;
    private final String port;
    private final AtomicBoolean isBusy = new AtomicBoolean(false);
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public MusicCreationResult createMusic(MusicCreationQueueItem item) {
        if (isBusy.compareAndSet(false, true)) {
            try {
                MusicDto.CreateRequest requestBodyDto = MusicDto.CreateRequest.builder()
                        .genre(item.getGenre())
                        .lyrics(item.getLyrics())
                        .build();

                String requestBodyJson = objectMapper.writeValueAsString(requestBodyDto);

                RequestBody body = RequestBody.create(requestBodyJson, JSON);

                String url = String.format("%s:%s/create", host, port);

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = Objects.requireNonNull(response.body()).string();
                        log.error("Music creation failed for item {}: {} {}", item.getDiaryId(), response.code(),
                                errorBody);
                        throw new RestException(ErrorCode.AI_MUSIC_CREATION_FAILED); // assuming this ErrorCode exists
                    }
                    log.info("Music creation successful for item: {}", item.getDiaryId());

                    // Return the music file stream and diaryId
                    return new MusicCreationResult(item.getDiaryId(), response.body().byteStream());

                }
            } catch (Exception e) {
                log.error("Exception during music creation request for item {}", item.getDiaryId(), e);
                throw new RestException(ErrorCode.AI_MUSIC_CREATION_FAILED); // assuming this ErrorCode exists
            } finally {
                isBusy.set(false);
            }
        } else {
            // This case should ideally not happen with the queue processing logic,
            // but as a fallback, you might want to re-add the item to the queue or handle
            // it differently.
            // For now, returning null or throwing an exception.
            log.warn("Music server node {} is busy. Item {} could not be processed immediately.", host + ":" + port,
                    item.getDiaryId());
            // Re-add to queue or handle error appropriately
            throw new RestException(ErrorCode.AI_MUSIC_CREATION_FAILED); // Or a specific node busy error
        }
    }
}
