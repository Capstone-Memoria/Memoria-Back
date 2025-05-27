package ac.mju.memoria.backend.domain.diarybook.controller;

import ac.mju.memoria.backend.domain.diarybook.dto.StickerDto;
import ac.mju.memoria.backend.domain.diarybook.service.StickerService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Sticker", description = "다이어리 북 스티커 API")
public class StickerController {
    private final StickerService stickerService;

    @PutMapping("/diary-book/{diaryBookId}/stickers")
    public List<StickerDto.AbstractResponse> updateStickers(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Valid @RequestBody StickerDto.UpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        return stickerService.updateStickers(diaryBookId, request, userDetails);
    }

    @PostMapping("/stickers/images/hold")
    public StickerDto.HoldStickerImageResponse holdStickerImage(
                @Valid @ModelAttribute StickerDto.HoldStickerImageRequest request) {

        return StickerDto.HoldStickerImageResponse.from(stickerService.holdStickerImage(request));
    }
}
