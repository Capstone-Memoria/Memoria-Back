package ac.mju.memoria.backend.domain.ai.service;

import ac.mju.memoria.backend.domain.ai.dto.MusicDto;
import ac.mju.memoria.backend.domain.ai.llm.model.LyricsResponse;
import ac.mju.memoria.backend.domain.ai.llm.service.LyricsGenerator;
import ac.mju.memoria.backend.domain.ai.llm.service.MusicPromptGenerator;
import ac.mju.memoria.backend.domain.ai.networking.DefaultNode;
import ac.mju.memoria.backend.domain.ai.networking.music.MusicNodePool;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.file.entity.MusicFile;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.AttachedFileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class MusicCreateService {
    private final LyricsGenerator lyricsGenerator;
    private final MusicPromptGenerator musicPromptGenerator;
    private final MusicNodePool musicNodePool;
    private final DiaryRepository diaryRepository;
    private final FileSystemHandler fileSystemHandler;
    private final AttachedFileRepository attachedFileRepository;

    public MusicCreateService(
            LyricsGenerator lyricsGenerator,
            MusicPromptGenerator musicPromptGenerator,
            @Qualifier("musicServerNodes") List<DefaultNode> musicServerNodes,
            ObjectMapper objectMapper,
            DiaryRepository diaryRepository, FileSystemHandler fileSystemHandler, AttachedFileRepository attachedFileRepository) {
        this.lyricsGenerator = lyricsGenerator;
        this.musicPromptGenerator = musicPromptGenerator;
        this.diaryRepository = diaryRepository;

        this.musicNodePool = new MusicNodePool(objectMapper);

        musicNodePool.start();
        musicServerNodes.forEach(musicNodePool::addNode);

        this.fileSystemHandler = fileSystemHandler;
        this.attachedFileRepository = attachedFileRepository;
    }


    @Async
    public void requestMusic(Diary diary) {
        String genre = musicPromptGenerator.generateMusicPrompt(diary.getTitle(), diary.getContent());
        LyricsResponse lyrics = lyricsGenerator.generateLyrics(diary.getTitle(), diary.getContent(), genre);

        MusicDto.CreateRequest createRequest = MusicDto.CreateRequest.of(
                genre,
                lyrics.getLyrics()
        );
        musicNodePool.submitRequest(createRequest, (data) -> handleOnMusicCreated(diary.getId(), data));
    }

    @SneakyThrows
    protected void handleOnMusicCreated(Long diaryId, byte[] data) {
        Optional<Diary> found = diaryRepository.findById(diaryId);
        if(found.isEmpty()) {
            log.warn("Could not find diary with id {}", diaryId);
            return;
        }

        MusicFile musicFile = MusicFile.builder()
                .diary(found.get())
                .fileType(FileType.DOWNLOADABLE)
                .fileName(found.get().getTitle()+".mp3")
                .size(0L) // Temporary
                .id(UUID.randomUUID().toString())
                .build();

        InputStream inputStream = new ByteArrayInputStream(data);
        long size = fileSystemHandler.saveStream(inputStream, musicFile);
        musicFile.setSize(size);

        attachedFileRepository.save(musicFile);
    }
}
