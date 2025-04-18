package ac.mju.memoria.backend.domain.file.controller;

import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.file.dto.StickerDto;
import ac.mju.memoria.backend.domain.file.entity.Sticker;
import ac.mju.memoria.backend.domain.file.service.StickerService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StickerController {
    private final StickerService stickerService;

    @PostMapping("/stickers")
    public ResponseEntity<StickerDto.StickerResponse> createSticker(
            @Valid @ModelAttribute StickerDto.StickerCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        StickerDto.StickerResponse sticker = stickerService.createSticker(request, userDetails);
        return ResponseEntity.ok(sticker);
    }

    @PostMapping("/diary-book/{diaryBookId}/stickers/{id}")
    public ResponseEntity<StickerDto.StickerResponse> addSticker(
            @PathVariable Long diaryBookId,
            @PathVariable String id,
            @Valid @RequestBody StickerDto.StickerAddRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        StickerDto.StickerResponse sticker = stickerService.addSticker(diaryBookId, id, request, userDetails);
        return ResponseEntity.ok(sticker);
    }

    @PatchMapping("/diary-book/{diaryBookId}/stickers/{id}")
    public ResponseEntity<StickerDto.StickerResponse> updateSticker(
            @PathVariable Long diaryBookId,
            @PathVariable String id,
            @Valid @RequestBody StickerDto.StickerAddRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        StickerDto.StickerResponse updatedSticker = stickerService.updateSticker(diaryBookId, id, request, userDetails);
        return ResponseEntity.ok(updatedSticker);
    }

    @DeleteMapping("/diary-book/{diaryBookId}/stickers/{id}")
    public ResponseEntity<Void> deleteSticker(
            @PathVariable Long diaryBookId,
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {

        stickerService.deleteSticker(diaryBookId, id, userDetails);
        return ResponseEntity.noContent().build();
    }
}
