package ac.mju.memoria.backend.domain.diarybook.service;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookDto;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.UserDiaryBookPin;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.UserDiaryBookPinRepository;
import ac.mju.memoria.backend.domain.file.entity.CoverImageFile;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.AttachedFileRepository;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.domain.user.repository.UserRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class DiaryBookService {
    private final DiaryBookRepository diaryBookRepository;
    private final DiaryBookQueryRepository diaryBookQueryRepository;
    private final UserRepository userRepository;
    private final AttachedFileRepository attachedFileRepository;
    private final FileSystemHandler fileSystemHandler;
    private final UserDiaryBookPinRepository userDiaryBookPinRepository;

    @Transactional
    public DiaryBookDto.DiaryBookResponse createDiaryBook(DiaryBookDto.DiaryBookCreateRequest request,
            UserDetails userDetails) {
        String userEmail = userDetails.getKey();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RestException(ErrorCode.AUTH_USER_NOT_FOUND));

        DiaryBook diaryBook = request.toEntity(user);

        if (Objects.nonNull(request.getCoverImage())) {
            CoverImageFile coverImage = CoverImageFile.from(request.getCoverImage());
            diaryBook.setCoverImageFile(coverImage);
            coverImage.setDiaryBook(diaryBook);
            fileSystemHandler.saveFile(request.getCoverImage(), coverImage);
        }

        user.addOwnedDiaryBook(diaryBook);
        DiaryBook saved = diaryBookRepository.save(diaryBook);

        return DiaryBookDto.DiaryBookResponse.from(saved, false);
    }

    public DiaryBookDto.DiaryBookResponse findDiaryBook(Long diaryBookId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));
        boolean isPinned = userDiaryBookPinRepository.findByUserEmailAndDiaryBookId(userDetails.getKey(), diaryBookId)
                .map(UserDiaryBookPin::isPinned)
                .orElse(false);

        return DiaryBookDto.DiaryBookResponse.from(diaryBook, isPinned);
    }

    public Page<DiaryBookDto.DiaryBookResponse> getMyDiaryBooks(UserDetails userDetails, Pageable pageable) {
        Page<DiaryBook> diaryBookPage = diaryBookQueryRepository.findByUserEmailWithDetails(userDetails.getKey(),
                pageable);

        return diaryBookPage.map(diaryBook -> {
            boolean isPinned = userDiaryBookPinRepository
                    .findByUserEmailAndDiaryBookId(userDetails.getKey(), diaryBook.getId())
                    .map(UserDiaryBookPin::isPinned)
                    .orElse(false);
            return DiaryBookDto.DiaryBookResponse.from(diaryBook, isPinned);
        });
    }

    @Transactional
    public DiaryBookDto.DiaryBookResponse updateDiaryBook(DiaryBookDto.DiaryBookUpdateRequest request, Long diaryBookId,
            UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        updateCoverImageIfNotNull(request, diaryBook);
        request.applyTo(diaryBook);

        boolean isPinned = userDiaryBookPinRepository.findByUserEmailAndDiaryBookId(userDetails.getKey(), diaryBookId)
                .map(UserDiaryBookPin::isPinned)
                .orElse(false);
        return DiaryBookDto.DiaryBookResponse.from(diaryBook, isPinned);
    }

    private void updateCoverImageIfNotNull(DiaryBookDto.DiaryBookUpdateRequest request, DiaryBook diaryBook) {
        if (Objects.nonNull(request.getCoverImage())) {
            attachedFileRepository.delete(diaryBook.getCoverImageFile());

            CoverImageFile newCoverImage = CoverImageFile.from(request.getCoverImage());
            diaryBook.setCoverImageFile(newCoverImage);
            newCoverImage.setDiaryBook(diaryBook);

            fileSystemHandler.saveFile(request.getCoverImage(), newCoverImage);
            fileSystemHandler.deleteFile(diaryBook.getCoverImageFile());
        }
    }

    @Transactional
    public void deleteDiaryBook(Long diaryBookId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        userDiaryBookPinRepository.findByUserEmailAndDiaryBookId(userDetails.getKey(), diaryBookId)
                .ifPresent(userDiaryBookPinRepository::delete);

        diaryBookRepository.delete(diaryBook);
    }

    @Transactional
    public DiaryBookDto.DiaryBookResponse togglePin(Long diaryBookId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.AUTH_USER_NOT_FOUND));
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        UserDiaryBookPin userDiaryBookPin = userDiaryBookPinRepository
                .findByUserEmailAndDiaryBookId(user.getEmail(), diaryBook.getId())
                .orElseGet(() -> {
                    UserDiaryBookPin newPin = UserDiaryBookPin.builder()
                            .user(user)
                            .diaryBook(diaryBook)
                            .isPinned(false)
                            .build();
                    return userDiaryBookPinRepository.save(newPin);
                });

        userDiaryBookPin.setPinned(!userDiaryBookPin.isPinned());
        userDiaryBookPinRepository.save(userDiaryBookPin);

        return DiaryBookDto.DiaryBookResponse.from(diaryBook, userDiaryBookPin.isPinned());
    }
}
