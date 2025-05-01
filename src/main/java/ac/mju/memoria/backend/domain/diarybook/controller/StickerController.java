package ac.mju.memoria.backend.domain.diarybook.controller;

import ac.mju.memoria.backend.domain.diarybook.dto.StickerDto;
import ac.mju.memoria.backend.domain.diarybook.service.StickerService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book/{diaryBookId}/stickers")
@Tag(name = "Sticker", description = "다이어리 북 스티커 API")
public class StickerController {
    private final StickerService stickerService;

    @PutMapping
    @Operation(summary = "다이어리 북 스티커 업데이트", description = "특정 다이어리 북에 부착된 스티커 목록을 업데이트합니다. 요청 본문에 포함된 스티커 목록으로 전체 교체됩니다.")
    @ApiResponse(responseCode = "200", description = "스티커 업데이트 성공")
    public ResponseEntity<List<StickerDto.StickerResponse>> updateStickers(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Valid @RequestBody StickerDto.StickerUpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        List<StickerDto.StickerResponse> updated = stickerService.updateStickers(diaryBookId, request, userDetails);
        return ResponseEntity.ok(updated);
    }
}
