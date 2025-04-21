package ac.mju.memoria.backend.domain.diarybook.controller;

import ac.mju.memoria.backend.domain.diarybook.dto.StickerDto;
import ac.mju.memoria.backend.domain.diarybook.service.StickerService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book/{diaryBookId}/stickers")
public class StickerController {
    private final StickerService stickerService;

    @PutMapping
    public ResponseEntity<List<StickerDto.StickerResponse>> updateStickers(
            @PathVariable Long diaryBookId,
            @Valid @RequestBody StickerDto.StickerUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<StickerDto.StickerResponse> updated = stickerService.updateStickers(diaryBookId, request, userDetails);
        return ResponseEntity.ok(updated);
    }
}
