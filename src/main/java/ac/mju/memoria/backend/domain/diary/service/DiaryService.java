package ac.mju.memoria.backend.domain.diary.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ac.mju.memoria.backend.domain.ai.service.ImageCreateService;
import ac.mju.memoria.backend.domain.ai.service.MusicCreateService;
import ac.mju.memoria.backend.domain.diary.dto.DiaryDto;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.event.AiCommentNeededEvent;
import ac.mju.memoria.backend.domain.diary.event.DiaryCreatedEvent;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.domain.file.entity.Image;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.ImageRepository;
import ac.mju.memoria.backend.domain.notification.event.NewDiaryEvent;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryBookRepository diaryBookRepository;
    private final ImageRepository imageRepository;
    private final FileSystemHandler fileSystemHandler;
    private final ApplicationEventPublisher eventPublisher;
    private final MusicCreateService musicCreateService;
    private final ImageCreateService imageCreateService;

    @Transactional
    public DiaryDto.DiaryResponse createDiary(Long diaryBookId, DiaryDto.DiaryRequest requestDto,
            UserDetails userDetails) {
        User user = userDetails.getUser();

        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        diaryBook.isMember(user);

        Diary diary = Diary.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .emotion(requestDto.getEmotion())
                .author(user)
                .diaryBook(diaryBook)
                .aiMusicEnabled(requestDto.getIsAIMusicEnabled())
                .build();

        diaryBook.addDiary(diary);
        user.addDiary(diary);
        Diary saved = diaryRepository.save(diary);

        List<MultipartFile> images = requestDto.getImages();

        if (images != null && !images.isEmpty()) {
            List<Image> savedImages = addImages(images, diary);
            savedImages.forEach(saved::addImage);
        }

        if (requestDto.getIsAICommentEnabled()) {
            eventPublisher
                    .publishEvent(AiCommentNeededEvent.of(this, saved.getId(), requestDto.getDesiredCharacterId()));
            eventPublisher.publishEvent(new NewDiaryEvent(saved.getId()));
        }

        if (requestDto.getIsAIMusicEnabled()) {
            musicCreateService.requestMusic(saved);
        }

        if (requestDto.getImages() == null || requestDto.getImages().isEmpty()) {
            imageCreateService.requestGenerateImageFrom(saved);
        }
        eventPublisher.publishEvent(new DiaryCreatedEvent(this, diaryBookId));

        return DiaryDto.DiaryResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public DiaryDto.DiaryResponse getDiary(Long diaryBookId, Long diaryId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        diaryBook.isMember(userDetails.getUser());

        Diary diary = diaryRepository.findByIdAndDiaryBook(diaryId, diaryBook)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        return DiaryDto.DiaryResponse.from(diary);
    }

    @Transactional(readOnly = true)
    public Page<DiaryDto.DiaryResponse> getDiariesByDiaryBook(Long diaryBookId, UserDetails userDetails,
            Pageable pageable) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        diaryBook.isMember(userDetails.getUser());

        Page<Diary> diaryPage = diaryRepository.findByDiaryBook(diaryBook, pageable);

        return diaryPage.map(DiaryDto.DiaryResponse::from);
    }

    @Transactional(readOnly = true)
    public List<DiaryDto.DiaryResponse> getDiariesByDateRange(Long diaryBookId, LocalDate startDate, LocalDate endDate,
            UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        diaryBook.isMember(userDetails.getUser());

        List<Diary> diaries = diaryRepository.findByDiaryBookAndCreatedAtBetween(diaryBook, startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay());

        return diaries.stream()
                .map(DiaryDto.DiaryResponse::from)
                .toList();
    }

    @Transactional
    public DiaryDto.DiaryResponse updateDiary(Long diaryBookId, Long diaryId, DiaryDto.DiaryUpdateRequest requestDto,
            UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        diaryBook.isMember(userDetails.getUser());

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        diary.canUpdateAndDelete(userDetails.getUser());

        List<MultipartFile> toAddImages = requestDto.getToAddImages();
        if (toAddImages != null && !toAddImages.isEmpty()) {
            List<Image> addedImages = addImages(toAddImages, diary);
            addedImages.forEach(diary::addImage);
        }

        List<String> toDeleteImageIds = requestDto.getToDeleteImageIds();
        if (toDeleteImageIds != null && !toDeleteImageIds.isEmpty()) {
            toDeleteImageIds.stream()
                    .filter(Objects::nonNull)
                    .forEach(id -> {
                        Image toDelete = imageRepository.findById(id)
                                .orElseThrow(() -> new RestException(ErrorCode.FILE_NOT_FOUND));
                        diary.getImages().remove(toDelete);
                        fileSystemHandler.deleteFile(toDelete);
                    });
        }

        Diary updated = requestDto.applyTo(requestDto, diary);
        eventPublisher.publishEvent(new DiaryCreatedEvent(this, diaryBookId));
        return DiaryDto.DiaryResponse.from(updated);
    }

    @Transactional
    public void deleteDiary(Long diaryBookId, Long diaryId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        User user = userDetails.getUser();

        diaryBook.isMember(user);

        Diary diary = diaryRepository.findByIdAndDiaryBook(diaryId, diaryBook)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        diary.canUpdateAndDelete(user);

        List<Image> images = diary.getImages();
        if (!images.isEmpty()) {
            images.forEach(fileSystemHandler::deleteFile);
        }

        if (diary.getMusicFile() != null) {
            fileSystemHandler.deleteFile(diary.getMusicFile());
        }

        diaryRepository.delete(diary);
        eventPublisher.publishEvent(new DiaryCreatedEvent(this, diaryBookId));
    }

    private List<Image> addImages(List<MultipartFile> images, Diary diary) {
        return images.stream()
                .filter(file -> file != null && !file.isEmpty())
                .map(file -> {
                    Image image = Image.from(file, diary);
                    if (image != null) {
                        fileSystemHandler.saveFile(file, image);
                        return imageRepository.save(image);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
