package ac.mju.memoria.backend.domain.diarybook.service;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookDto;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.domain.user.repository.UserRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
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
    private final UserRepository userRepository;

    @Transactional
    public DiaryBookDto.DiaryBookResponse createDiaryBook(DiaryBookDto.DiaryBookCreateRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RestException(ErrorCode.USER_NOT_FOUND));

        DiaryBook diaryBook = request.toEntity();
        user.addDiaryBook(diaryBook);
        diaryBookRepository.save(diaryBook);

        return DiaryBookDto.DiaryBookResponse.from(diaryBook);
    }

    public DiaryBookDto.DiaryBookResponse getDiaryBook(Integer diaryBookId, String userEmail) {
        DiaryBook diaryBook = diaryBookRepository.findByIdAndUserEmail(diaryBookId, userEmail)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        return DiaryBookDto.DiaryBookResponse.from(diaryBook);
    }

    public Page<DiaryBookDto.DiaryBookResponse> getMyDiaryBooks(String userEmail, Pageable pageable) {
        Sort primarySort = Sort.by(Sort.Direction.DESC, "isPinned");
        Sort secondarySort = pageable.getSort();
        Sort finalSort = primarySort.and(secondarySort);
        Pageable finalPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), finalSort);

        Page<DiaryBook> diaryBookPage = diaryBookRepository.findByUserEmailWithDetails(userEmail, finalPageable);

        return diaryBookPage.map(DiaryBookDto.DiaryBookResponse::from);
    }

    @Transactional
    public DiaryBookDto.DiaryBookResponse updateDiaryBook(DiaryBookDto.DiaryBookUpdateRequest request, Integer diaryBookId, String userEmail) {
        DiaryBook diaryBook = diaryBookRepository.findByIdAndUserEmail(diaryBookId, userEmail)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        request.applyTo(diaryBook);

        return DiaryBookDto.DiaryBookResponse.from(diaryBook);
    }

    @Transactional
    public void deleteDiaryBook(Integer diaryBookId, String userEmail) {
        int affectedRows = diaryBookRepository.deleteByIdAndUserEmail(diaryBookId, userEmail);

        if (affectedRows == 0) {
            throw new RestException(ErrorCode.DIARYBOOK_NOT_FOUND);
        }
    }

}
