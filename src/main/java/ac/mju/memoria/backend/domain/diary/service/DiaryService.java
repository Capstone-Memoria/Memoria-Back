package ac.mju.memoria.backend.domain.diary.service;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ac.mju.memoria.backend.domain.ai.service.MusicCreateService;
import ac.mju.memoria.backend.domain.diary.dto.DiaryDto;
import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.repository.DiaryRepository;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.domain.file.entity.Image;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.ImageRepository;
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
    private final MusicCreateService musicCreateService;

    @Transactional
    public DiaryDto.DiaryResponse createDiary(Long diaryBookId, DiaryDto.DiaryRequest requestDto,
            UserDetails userDetails) {
        User user = userDetails.getUser();

        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        diaryBook.isAdmin(user);

        Diary diary = Diary.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .author(user)
                .diaryBook(diaryBook)
                .build();

        diaryBook.addDiary(diary);
        user.addDiary(diary);
        Diary saved = diaryRepository.save(diary);

        List<MultipartFile> images = requestDto.getImages();

        if (images != null && !images.isEmpty()) {
            List<Image> savedImages = addImages(images, diary);
            savedImages.forEach(saved::addImage);
        }

        // 음악 생성 프롬프트 생성 및 큐에 추가
//        musicCreateService.addToQueue(saved); TODO: 음악 생성 프롬프트 큐에 추가

        return DiaryDto.DiaryResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public DiaryDto.DiaryResponse getDiary(Long diaryBookId, Long diaryId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        diaryBook.isAdmin(userDetails.getUser());

        Diary diary = diaryRepository.findByIdAndDiaryBook(diaryId, diaryBook)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        return DiaryDto.DiaryResponse.fromEntity(diary);
    }

    @Transactional(readOnly = true)
    public Page<DiaryDto.DiaryResponse> getDiariesByDiaryBook(Long diaryBookId, UserDetails userDetails,
            Pageable pageable) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        diaryBook.isAdmin(userDetails.getUser());

        Page<Diary> diaryPage = diaryRepository.findByDiaryBook(diaryBook, pageable);

        return diaryPage.map(DiaryDto.DiaryResponse::fromEntity);
    }

    @Transactional
    public DiaryDto.DiaryResponse updateDiary(Long diaryBookId, Long diaryId, DiaryDto.DiaryUpdateRequest requestDto,
            UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        diaryBook.isAdmin(userDetails.getUser());

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

        return DiaryDto.DiaryResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteDiary(Long diaryBookId, Long diaryId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        User user = userDetails.getUser();

        diaryBook.isAdmin(user);

        Diary diary = diaryRepository.findByIdAndDiaryBook(diaryId, diaryBook)
                .orElseThrow(() -> new RestException(ErrorCode.DIARY_NOT_FOUND));

        diary.canUpdateAndDelete(user);

        List<Image> images = diary.getImages();
        if (!images.isEmpty()) {
            images.forEach(fileSystemHandler::deleteFile);
        }

        diaryRepository.delete(diary);
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
