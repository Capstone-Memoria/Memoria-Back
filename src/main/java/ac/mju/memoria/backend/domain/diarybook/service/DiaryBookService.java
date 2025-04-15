package ac.mju.memoria.backend.domain.diarybook.service;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookDto;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.file.dto.StickerDto;
import ac.mju.memoria.backend.domain.file.entity.CoverImageFile;
import ac.mju.memoria.backend.domain.file.entity.Sticker;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DiaryBookService {
    private final DiaryBookRepository diaryBookRepository;
    private final DiaryBookQueryRepository diaryBookQueryRepository;
    private final UserRepository userRepository;
    private final AttachedFileRepository attachedFileRepository;
    private final FileSystemHandler fileSystemHandler;

    @Transactional
    public DiaryBookDto.DiaryBookResponse createDiaryBook(DiaryBookDto.DiaryBookCreateRequest request, UserDetails userDetails) {
        String userEmail = userDetails.getKey();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RestException(ErrorCode.AUTH_USER_NOT_FOUND));

        CoverImageFile coverImage = CoverImageFile.from(request.getCoverImage());
        DiaryBook diaryBook = request.toEntity();

        diaryBook.setCoverImageFile(coverImage);
        coverImage.setDiaryBook(diaryBook);

        user.addOwnedDiaryBook(diaryBook);
        diaryBookRepository.save(diaryBook);
        fileSystemHandler.saveFile(request.getCoverImage(), coverImage);

        return DiaryBookDto.DiaryBookResponse.from(diaryBook);
    }

    public DiaryBookDto.DiaryBookResponse findDiaryBook(Long diaryBookId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        return DiaryBookDto.DiaryBookResponse.from(diaryBook);
    }

    public Page<DiaryBookDto.DiaryBookResponse> getMyDiaryBooks(UserDetails userDetails, Pageable pageable) {
        Sort primarySort = Sort.by(Sort.Direction.DESC, "isPinned");
        Sort secondarySort = pageable.getSort();
        Sort finalSort = primarySort.and(secondarySort);
        Pageable finalPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), finalSort);

        Page<DiaryBook> diaryBookPage = diaryBookQueryRepository.findByUserEmailWithDetails(userDetails.getKey(), finalPageable);

        return diaryBookPage.map(DiaryBookDto.DiaryBookResponse::from);
    }

    @Transactional
    public DiaryBookDto.DiaryBookResponse updateDiaryBook(DiaryBookDto.DiaryBookUpdateRequest request, Long diaryBookId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        updateCoverImageIfNotNull(request, diaryBook);
        request.applyTo(diaryBook);

        return DiaryBookDto.DiaryBookResponse.from(diaryBook);
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

        diaryBookRepository.delete(diaryBook);
    }
}
