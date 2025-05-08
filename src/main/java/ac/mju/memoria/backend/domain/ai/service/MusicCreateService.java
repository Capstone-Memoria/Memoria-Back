package ac.mju.memoria.backend.domain.ai.service;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ac.mju.memoria.backend.domain.ai.model.MusicCreationQueueItem;
import ac.mju.memoria.backend.domain.ai.model.MusicCreationResult;
import ac.mju.memoria.backend.domain.ai.model.MusicServerNode;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.file.entity.MusicFile;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.AttachedFileRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MusicCreateService {
    private final Queue<MusicCreationQueueItem> creationQueue = new LinkedList<>();
    private final List<MusicServerNode> musicServerNodes;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final DiaryRepository diaryRepository;
    private final AttachedFileRepository attachedFileRepository;
    private final FileSystemHandler fileSystemHandler;

    @Value("${file.save-path}")
    private String fileSavePath;

    public void addToQueue(MusicCreationQueueItem item) {
        creationQueue.add(item);
        log.info("Item added to queue: {}", item.getDiaryId());
        executorService.submit(this::processQueue);
    }

    private void processQueue() {
        if (creationQueue.isEmpty()) {
            return;
        }

        while (!creationQueue.isEmpty()) {
            Optional<MusicServerNode> availableNode = musicServerNodes.stream()
                    .filter(node -> !node.getIsBusy().get())
                    .findFirst();

            if (availableNode.isPresent()) {
                MusicServerNode node = availableNode.get();
                MusicCreationQueueItem item = creationQueue.poll();
                if (item != null) {
                    log.info("Processing item {} using node {}", item.getDiaryId(),
                            node.getHost() + ":" + node.getPort());
                    try {
                        MusicCreationResult result = node.createMusic(item);
                        handleMusicCreationResult(result);
                    } catch (Exception e) {
                        log.error("Error processing music creation for item {}", item.getDiaryId(), e);
                    }
                }
            } else {
                log.info("No available music server nodes. Waiting...");
                break;
            }
        }
    }

    private void handleMusicCreationResult(MusicCreationResult result) {
        try (InputStream is = result.getMusicFileStream()) {
            Optional<Diary> optionalDiary = diaryRepository.findById(result.getDiaryId());
            if (optionalDiary.isEmpty()) {
                log.error("Diary with ID {} not found for music creation result.", result.getDiaryId());
                return;
            }
            Diary diary = optionalDiary.get();

            MusicFile musicFile = MusicFile.builder()
                    .id(UUID.randomUUID().toString())
                    .fileName(UUID.randomUUID().toString() + ".mp3")
                    .size(0L)
                    .fileType(FileType.DOWNLOADABLE)
                    .diary(diary)
                    .build();

            long actualSize = fileSystemHandler.saveStream(is, musicFile);

            musicFile.setSize(actualSize);

            MusicFile savedMusicFile = attachedFileRepository.save(musicFile);

            diary.setMusicFile(savedMusicFile);
            diaryRepository.save(diary);

            log.info("MusicFile entity saved and linked to diary {}: {}", diary.getId(), savedMusicFile.getId());

        } catch (Exception e) {
            log.error("Error handling music creation result for diary {}", result.getDiaryId(), e);
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down executor service...");
        executorService.shutdown();
    }

    public boolean isAnyNodeBusy() {
        return musicServerNodes.stream()
                .anyMatch(node -> node.getIsBusy().get());
    }
}
