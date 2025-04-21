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
import java.util.stream.Collectors;

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

        List<Sticker> stickers = request.getStickers().stream()
                .map(info -> {
                    Sticker sticker = info.toEntity();
                    sticker.setDiaryBook(diaryBook);
                    return sticker;
                })
                .collect(Collectors.toList());

        List<Sticker> savedStickers = stickerRepository.saveAll(stickers);

        return savedStickers.stream()
                .map(StickerDto.StickerResponse::from)
                .collect(Collectors.toList());
    }
}