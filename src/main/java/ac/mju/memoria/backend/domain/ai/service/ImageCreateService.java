package ac.mju.memoria.backend.domain.ai.service;

import ac.mju.memoria.backend.domain.ai.dto.ImageDto;
import ac.mju.memoria.backend.domain.ai.llm.model.StableDiffusionPrompts;
import ac.mju.memoria.backend.domain.ai.llm.service.PromptGenerator;
import ac.mju.memoria.backend.domain.ai.networking.image.ImageNodePool;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.file.entity.Image;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.ImageRepository;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCreateService {
    private final GoogleAiGeminiChatModel chatModel;
    private final ImageNodePool imageNodePool;
    private final DiaryRepository diaryRepository;
    private final FileSystemHandler fileSystemHandler;
    private final ImageRepository imageRepository;
    private final TransactionTemplate transactionTemplate;

    @PostConstruct
    private void init() {
        imageNodePool.start();
    }

    @SneakyThrows
    @Async
    public void requestGenerateImageFrom(Diary diary) {
        ImageDto.InternalCreateRequest req = buildChatRequest(diary);
        imageNodePool.submitRequest(req, (response) -> handleDiaryCoverCreated(diary.getId(), response));
    }

    public void handleDiaryCoverCreated(Long diaryId, String imageData) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                Optional<Diary> found = diaryRepository.findById(diaryId);

                if (found.isEmpty()) {
                    log.error("Diary with ID {} not found", diaryId);
                    return;
                }

                byte[] decoded = Base64.getDecoder().decode(imageData);
                InputStream inputStream = new ByteArrayInputStream(decoded);

                Image image = Image.builder()
                        .id(UUID.randomUUID().toString())
                        .fileName("diary_cover_" + diaryId + ".png")
                        .size(0L) // temporary
                        .fileType(FileType.IMAGE)
                        .diary(found.get())
                        .build();

                long size = fileSystemHandler.saveStream(inputStream, image);
                image.setSize(size);

                Image savedImage = imageRepository.save(image);
                found.get().addImage(savedImage);
            }
        });
    }

    @SneakyThrows
    public String generateImage(ImageDto.CreateRequest request) {
        ImageDto.InternalCreateRequest req = buildChatRequest(
                request.getDescription());

        Future<String> response = imageNodePool.submitRequest(req);
        return response.get();
    }

    @SneakyThrows
    private ImageDto.InternalCreateRequest buildChatRequest(
            String description) {
        PromptGenerator promptGenerator = AiServices.builder(
                PromptGenerator.class)
                .chatLanguageModel(chatModel)
                .build();

        StableDiffusionPrompts result = promptGenerator.generatePrompts(
                description);

        return ImageDto.InternalCreateRequest.builder()
                .prompt(result.prompt())
                .negativePrompt(result.negative_prompt())
                .build();
    }

    @SneakyThrows
    private ImageDto.InternalCreateRequest buildChatRequest(
            Diary diary) {
        PromptGenerator promptGenerator = AiServices.builder(
                PromptGenerator.class)
                .chatLanguageModel(chatModel)
                .build();

        StableDiffusionPrompts result = promptGenerator.generatePromptsFromDiary(
                diary.getTitle(),
                diary.getContent());

        return ImageDto.InternalCreateRequest.builder()
                .prompt(result.prompt())
                .negativePrompt(result.negative_prompt())
                .build();
    }
}
