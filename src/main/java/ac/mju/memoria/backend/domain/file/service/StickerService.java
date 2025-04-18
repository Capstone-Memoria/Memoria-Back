package ac.mju.memoria.backend.domain.file.service;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.domain.file.dto.StickerDto;
import ac.mju.memoria.backend.domain.file.entity.Sticker;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.StickerRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StickerService {
    private final DiaryBookQueryRepository diaryBookQueryRepository;
    private final StickerRepository stickerRepository;
    private final FileSystemHandler fileSystemHandler;

    @Transactional
    public StickerDto.StickerResponse createSticker(StickerDto.StickerCreateRequest request, UserDetails userDetails) {
        MultipartFile file = request.getFile();

        Sticker sticker = Sticker.builder()
                .id(UUID.randomUUID().toString())
                .stickerType(StickerType.NONE)
                .fileType(FileType.IMAGE)
                .fileName(file.getOriginalFilename())
                .size(file.getSize())
                .posX(0)
                .posY(0)
                .width(0)
                .height(0)
                .build();

        stickerRepository.save(sticker);
        fileSystemHandler.saveFile(file, sticker);

        return StickerDto.StickerResponse.from(sticker);
    }

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
    public StickerDto.StickerResponse updateSticker(Long diaryBookId, String stickerId, StickerDto.StickerAddRequest request, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        Sticker sticker = stickerRepository.findById(stickerId)
                .orElseThrow(() -> new RestException(ErrorCode.STICKER_NOT_FOUND));

        if (!sticker.getDiaryBook().getId().equals(diaryBook.getId())) {
            throw new RestException(ErrorCode.DIARYBOOK_NOT_FOUND);
        }

        sticker.setPosX(request.getPosX());
        sticker.setPosY(request.getPosY());
        sticker.setWidth(request.getWidth());
        sticker.setHeight(request.getHeight());

        stickerRepository.save(sticker);

        return StickerDto.StickerResponse.from(sticker);
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