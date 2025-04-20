package ac.mju.memoria.backend.domain.diarybook.service;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.domain.diarybook.dto.StickerDto;
import ac.mju.memoria.backend.domain.diarybook.entity.Sticker;
import ac.mju.memoria.backend.domain.diarybook.repository.StickerRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StickerService {
    private final DiaryBookQueryRepository diaryBookQueryRepository;
    private final StickerRepository stickerRepository;

    @Transactional
    public List<StickerDto.StickerResponse> updateStickers(Long diaryBookId, StickerDto.StickerUpdateRequest request, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        stickerRepository.deleteAllByDiaryBookId(diaryBook.getId());

        List<StickerDto.StickerResponse> responses = new ArrayList<>();
        for (StickerDto.StickerInfo info : request.getStickers()) {
            Sticker sticker = info.toEntity();
            sticker.setDiaryBook(diaryBook);

            Sticker saved = stickerRepository.save(sticker);
            responses.add(StickerDto.StickerResponse.from(saved));
        }
        return responses;
    }
}