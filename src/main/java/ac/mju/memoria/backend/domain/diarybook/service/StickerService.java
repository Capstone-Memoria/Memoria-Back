package ac.mju.memoria.backend.domain.diarybook.service;

import ac.mju.memoria.backend.domain.diarybook.dto.StickerDto;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.stickers.AbstractSticker;
import ac.mju.memoria.backend.domain.diarybook.entity.stickers.CustomImageSticker;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.StickerRepository;
import ac.mju.memoria.backend.domain.diarybook.service.handler.StickerImageHolder;
import ac.mju.memoria.backend.domain.file.entity.StickerImageFile;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class StickerService {
    private final StickerImageHolder stickerImageHolder = new StickerImageHolder();
    private final DiaryBookQueryRepository diaryBookQueryRepository;
    private final StickerRepository stickerRepository;
    private final FileSystemHandler fileSystemHandler;

    public String holdStickerImage(StickerDto.HoldStickerImageRequest request) {
        try {
            return stickerImageHolder.hold(request.getImageFile());
        } catch (IOException e) {
            log.error("Failed to hold sticker image", e);
            throw new RuntimeException("Failed to hold sticker image", e);
        }
    }

    @Transactional
    public List<StickerDto.AbstractResponse> updateStickers(Long diaryBookId, StickerDto.UpdateRequest request, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        removeExistingStickers(diaryBook);
        List<AbstractSticker> saved = createNewStickersFrom(request);

        return saved.stream()
                .map(StickerDto.AbstractResponse::from)
                .toList();
    }

    @NotNull
    private List<AbstractSticker> createNewStickersFrom(StickerDto.UpdateRequest request) {
        List<AbstractSticker> toSaves = request.getStickers().stream()
                .map(this::convertRequestToEntity)
                .toList();

        return stickerRepository.saveAll(toSaves);
    }

    private void removeExistingStickers(DiaryBook diaryBook) {
        diaryBook.getAbstractStickers().stream()
                .filter(it -> it.getType().equals(StickerType.CUSTOM_IMAGE))
                .map(CustomImageSticker.class::cast)
                .map(CustomImageSticker::getImageFile)
                .forEach(fileSystemHandler::deleteFile);
        stickerRepository.deleteAllByDiaryBookId(diaryBook.getId());
    }

    private AbstractSticker convertRequestToEntity(StickerDto.AbstractRequest it) {
        if (it.getType().equals(StickerType.CUSTOM_IMAGE)) {
            var req = (StickerDto.CustomImageStickerRequest) it;
            byte[] imageData = stickerImageHolder.findImage(req.getHeldStickerImageUuid())
                    .orElseThrow(() -> new RestException(ErrorCode.STICKER_IMAGE_NOT_FOUND));

            StickerImageFile stickerImageFile = StickerImageFile.ofNew();
            long size = fileSystemHandler.saveStream(new ByteArrayInputStream(imageData), stickerImageFile);
            stickerImageFile.setSize(size);

            CustomImageSticker entity = (CustomImageSticker) req.toEntity();
            entity.setImageFile(stickerImageFile);
            stickerImageFile.setSticker(entity);
            return entity;
        }

        return it.toEntity();
    }
}