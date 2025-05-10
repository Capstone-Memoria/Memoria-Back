package ac.mju.memoria.backend.domain.ai.service;

import ac.mju.memoria.backend.common.utils.JsonUtils;
import ac.mju.memoria.backend.domain.ai.dto.MusicDto;
import ac.mju.memoria.backend.domain.ai.llm.service.MusicPromptGenerator;
import ac.mju.memoria.backend.domain.ai.model.MusicCreationQueueItem;
import ac.mju.memoria.backend.domain.ai.model.MusicServerNode;
import ac.mju.memoria.backend.domain.ai.sse.SseWatcher;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.file.entity.MusicFile;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.AttachedFileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicCreateService {
    private final MusicPromptGenerator musicPromptGenerator;
    private final List<MusicServerNode> nodes;
    private final SseWatcher sseWatcher;
    private final DiaryRepository diaryRepository;
    private final FileSystemHandler fileSystemHandler;
    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final AttachedFileRepository attachedFileRepository;

    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private Thread scheduler;
    private final Queue<MusicCreationQueueItem> queue = new ConcurrentLinkedQueue<>();

    @PostConstruct
    public void init() {
        sseWatcher.addListener(response -> {
            List<String> completedJobs = response.getJobs().entrySet().stream()
                    .filter(entry -> entry.getValue().getStatus().equals("completed"))
                    .filter(entry -> nodes.stream()
                            .anyMatch(node -> node.getActiveJobId().equals(entry.getKey())))
                    .map(Map.Entry::getKey)
                    .toList();

            List<MusicServerNode> targets = completedJobs.stream()
                    .map(toRelatedNode())
                    .filter(node -> node != null && node.isBusy())
                    .toList();

            targets.forEach(this::handleCompletedJob);
        });

        scheduler = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    popAndCreateMusicIfNodeAvailable();
                } catch (InterruptedException e) {
                    log.error("Scheduler interrupted", e);
                    break;
                }
            }
        });
        scheduler.start();
    }

    private void popAndCreateMusicIfNodeAvailable() {
        MusicCreationQueueItem item = queue.poll();
        if (item == null) {
            return;
        }

        Optional<MusicServerNode> node = nodes.stream()
                .filter(n -> n.getActiveJobId() == null)
                .findFirst();
        if (node.isEmpty()) {
            return;
        }

        MusicServerNode musicServerNode = node.get();

        createMusic(musicServerNode, item);
    }

    @SneakyThrows
    private void createMusic(MusicServerNode node, MusicCreationQueueItem item) {
        Optional<Diary> found = diaryRepository.findById(item.getDiaryId());
        if (found.isEmpty()) {
            log.warn("Diary not found for ID: {}", item.getDiaryId());
            return;
        }

        String generatedGenre = musicPromptGenerator.generateMusicPrompt(found.get().getContent());

        RequestBody requestBody = RequestBody.create(
                JsonUtils.toJson(MusicDto.CreateRequest.from(generatedGenre)),
                MediaType.parse("application/json")
        );
        Request request = new Request.Builder()
                .url(node.getURL() + "/generate-music-async/")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to create music: {}", response.body().string());
                return;
            }

            String body = response.body().string();
            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode treeResult = objectMapper.readTree(body);
            String jobId = treeResult.get("job_id").asText();

            node.setActiveJobId(jobId);
            node.setBusy(true);
            node.setRelatedDiaryId(item.getDiaryId());
        }
    }

    public void requestMusic(Diary diary) {
        MusicCreationQueueItem item = MusicCreationQueueItem.builder()
                .diaryId(diary.getId())
                .build();
        queue.add(item);
        log.info("Added music creation request to queue: {}", item);
    }

    @Transactional
    @SneakyThrows
    public void handleCompletedJob(MusicServerNode node) {
        node.setBusy(false);
        log.info("Handling completed job for node: {}", node);

        Optional<Diary> found = diaryRepository.findById(node.getRelatedDiaryId());
        if (found.isEmpty()) {
            log.warn("Diary not found for node: {}", node);
            return;
        }

        Request request = new Request.Builder()
                .url(node.getURL() + "/music/download/" + node.getActiveJobId())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to download music: {}", response.body().string());
                return;
            }

            MusicFile musicFile = MusicFile.builder()
                    .diary(found.get())
                    .fileType(FileType.DOWNLOADABLE)
                    .fileName(found.get().getTitle() + ".mp3")
                    .size(0L) // Temporary
                    .id(UUID.randomUUID().toString())
                    .build();

            long size = fileSystemHandler.saveStream(response.body().byteStream(), musicFile);
            musicFile.setSize(size);

            attachedFileRepository.save(musicFile);
        }

        node.setActiveJobId(null);
        node.setRelatedDiaryId(null);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down executor service...");
        if (scheduler != null && scheduler.isAlive()) {
            scheduler.interrupt();
        }
        executorService.shutdown();
    }

    @NotNull
    private Function<String, MusicServerNode> toRelatedNode() {
        return jobId -> nodes.stream()
                .filter(node -> node.getActiveJobId().equals(jobId))
                .findFirst()
                .orElse(null);
    }
}
