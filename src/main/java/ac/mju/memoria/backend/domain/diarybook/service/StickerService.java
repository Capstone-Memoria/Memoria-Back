package ac.mju.memoria.backend.domain.diarybook.service;

import ac.mju.memoria.backend.domain.diarybook.dto.StickerDto;
import ac.mju.memoria.backend.domain.diarybook.entity.*;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.StickerRepository;
import ac.mju.memoria.backend.domain.file.entity.StickerImageFile;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.AttachedFileRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ac.mju.memoria.backend.domain.diarybook.dto.StickerDto.toAllStickerResponse;

@Service
@RequiredArgsConstructor
public class StickerService {
    private final DiaryBookQueryRepository diaryBookQueryRepository;
    private final StickerRepository stickerRepository;
    private final FileSystemHandler fileSystemHandler;
    private final AttachedFileRepository attachedFileRepository;

    @Transactional
    public StickerDto.AllStickersResponse updateStickers(Long diaryBookId, StickerDto.StickerUpdateRequest request, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        List<Sticker> existingStickers = stickerRepository.findAllByDiaryBookId(diaryBookId);
        for (Sticker existingSticker : existingStickers) {
            if (existingSticker instanceof CustomImageSticker imageSticker) {
                if (imageSticker.getImageFile() != null) {
                    fileSystemHandler.deleteFile(imageSticker.getImageFile());
                }
            }
        }
        stickerRepository.deleteAllByDiaryBookId(diaryBook.getId());

        List<Sticker> newStickersToSave = new ArrayList<>();

        if (Objects.nonNull(request.getPredefinedStickers())) {
            for (StickerDto.PredefinedStickerRequest predefinedRequest : request.getPredefinedStickers()) {
                PredefinedSticker sticker = buildPredefinedSticker(predefinedRequest, diaryBook);
                newStickersToSave.add(sticker);
            }
        }

        if (Objects.nonNull(request.getCustomImageStickers())) {
            for (StickerDto.CustomImageStickerCreateRequest imageRequest : request.getCustomImageStickers()) {
                StickerImageFile stickerImageFile = null;
                MultipartFile mpFile = imageRequest.getImageFile();

                if (mpFile != null && !mpFile.isEmpty()) {
                    stickerImageFile = StickerImageFile.from(mpFile);
                    fileSystemHandler.saveFile(mpFile, stickerImageFile);
                    attachedFileRepository.save(stickerImageFile);
                } else {
                    throw new RestException(ErrorCode.GLOBAL_INVALID_PARAMETER);
                }

                CustomImageSticker sticker = buildCustomImageSticker(imageRequest, diaryBook, stickerImageFile);
                newStickersToSave.add(sticker);
            }
        }

        if (Objects.nonNull(request.getCustomTextStickers())) {
            for (StickerDto.CustomTextStickerRequest textRequest : request.getCustomTextStickers()) {
                CustomTextSticker sticker = buildCustomTextSticker(textRequest, diaryBook);
                newStickersToSave.add(sticker);
            }
        }

        List<Sticker> saved = stickerRepository.saveAll(newStickersToSave);

        StickerDto.AllStickersResponse allStickersResponse = StickerDto.AllStickersResponse.builder().build();
        for (Sticker savedSticker : saved) {
            toAllStickerResponse(allStickersResponse, savedSticker);
        }
        return allStickersResponse;
    }

    private static CustomImageSticker buildCustomImageSticker(StickerDto.CustomImageStickerCreateRequest imageRequest, DiaryBook diaryBook, StickerImageFile stickerImageFile) {
        return CustomImageSticker.builder()
                .uuid(UUID.randomUUID().toString())
                .diaryBook(diaryBook)
                .stickerType(StickerType.CUSTOM_IMAGE)
                .posX(imageRequest.getPosX())
                .posY(imageRequest.getPosY())
                .size(imageRequest.getSize())
                .rotation(imageRequest.getRotation())
                .imageFile(stickerImageFile)
                .build();
    }

    private static CustomTextSticker buildCustomTextSticker(StickerDto.CustomTextStickerRequest textRequest, DiaryBook diaryBook) {
        return CustomTextSticker.builder()
                .uuid(UUID.randomUUID().toString())
                .diaryBook(diaryBook)
                .stickerType(StickerType.CUSTOM_TEXT)
                .posX(textRequest.getPosX())
                .posY(textRequest.getPosY())
                .size(textRequest.getSize())
                .rotation(textRequest.getRotation())
                .textContent(textRequest.getTextContent())
                .fontFamily(textRequest.getFontFamily())
                .fontSize(textRequest.getFontSize())
                .fontColor(textRequest.getFontColor())
                .backgroundColor(textRequest.getBackgroundColor())
                .build();
    }

    private static PredefinedSticker buildPredefinedSticker(StickerDto.PredefinedStickerRequest predefinedRequest, DiaryBook diaryBook) {
        return PredefinedSticker.builder()
                .uuid(predefinedRequest.getUuid())
                .diaryBook(diaryBook)
                .stickerType(StickerType.PREDEFINED)
                .posX(predefinedRequest.getPosX())
                .posY(predefinedRequest.getPosY())
                .size(predefinedRequest.getSize())
                .rotation(predefinedRequest.getRotation())
                .assetName(predefinedRequest.getAssetName())
                .build();
    }
}