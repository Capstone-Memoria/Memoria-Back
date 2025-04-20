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
    public StickerDto.StickerResponse addSticker(Long diaryBookId, String stickerId, StickerDto.StickerAddRequest request, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        Sticker sticker = stickerRepository.findById(stickerId)
                .orElseThrow(() -> new RestException(ErrorCode.STICKER_NOT_FOUND));

        sticker.setDiaryBook(diaryBook);
        sticker.setPosX(request.getPosX());
        sticker.setPosY(request.getPosY());
        sticker.setWidth(request.getWidth());
        sticker.setHeight(request.getHeight());

        stickerRepository.save(sticker);

        return StickerDto.StickerResponse.from(sticker);
    }

    @Transactional
    public List<StickerDto.StickerResponse> updateStickers(Long diaryBookId, StickerDto.StickerUpdateRequest request, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        stickerRepository.deleteAllByDiaryBookId(diaryBook.getId());

        List<StickerDto.StickerResponse> responses = new ArrayList<>();
        for (StickerDto.StickerAddRequest addRequest : request.getStickers()) {
            Sticker global = stickerRepository.findById(addRequest.getStickerId())
                    .orElseThrow(() -> new RestException(ErrorCode.STICKER_NOT_FOUND));

            global.setDiaryBook(diaryBook);
            global.setPosX(addRequest.getPosX());
            global.setPosY(addRequest.getPosY());
            global.setWidth(addRequest.getWidth());
            global.setHeight(addRequest.getHeight());

            stickerRepository.save(global);
            responses.add(StickerDto.StickerResponse.from(global));
        }

        return responses;
    }

    @Transactional
    public void deleteSticker(Long diaryBookId, String stickerId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        Sticker sticker = stickerRepository.findById(stickerId)
                .orElseThrow(() -> new RestException(ErrorCode.STICKER_NOT_FOUND));

        if (!sticker.getDiaryBook().getId().equals(diaryBook.getId())) {
            throw new RestException(ErrorCode.DIARYBOOK_NOT_FOUND);
        }

        stickerRepository.delete(sticker);
    }
}