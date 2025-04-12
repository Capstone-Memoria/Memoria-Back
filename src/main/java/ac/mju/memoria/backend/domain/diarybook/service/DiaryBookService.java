package ac.mju.memoria.backend.domain.diarybook.service;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookDto;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
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

@RequiredArgsConstructor
@Service
public class DiaryBookService {
    private final DiaryBookRepository diaryBookRepository;
    private final DiaryBookQueryRepository diaryBookQueryRepository;
    private final UserRepository userRepository;

    @Transactional
    public DiaryBookDto.DiaryBookResponse createDiaryBook(DiaryBookDto.DiaryBookCreateRequest request, UserDetails userDetails) {

        String userEmail = userDetails.getKey();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RestException(ErrorCode.AUTH_USER_NOT_FOUND));

        DiaryBook diaryBook = request.toEntity();
        user.addOwnedDiaryBook(diaryBook);
        diaryBookRepository.save(diaryBook);

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

        request.applyTo(diaryBook);

        return DiaryBookDto.DiaryBookResponse.from(diaryBook);
    }

    @Transactional
    public void deleteDiaryBook(Long diaryBookId, UserDetails userDetails) {

        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        diaryBookRepository.delete(diaryBook);
    }

}
