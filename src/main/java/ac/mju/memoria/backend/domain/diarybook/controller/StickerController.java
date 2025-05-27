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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book/{diaryBookId}/stickers")
@Tag(name = "Sticker", description = "다이어리 북 스티커 API")
public class StickerController {
    private final StickerService stickerService;

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "다이어리 북 스티커 업데이트",
            description = "특정 다이어리 북에 부착된 스티커 목록을 업데이트합니다. 요청 본문에 포함된 스티커 목록으로 전체 교체됩니다." +
                            "요청 시 각 스티커 타입(predefined, customImage, customText)별로 리스트를 전달합니다.")
    @ApiResponse(responseCode = "200", description = "스티커 업데이트 성공")
    public ResponseEntity<StickerDto.AllStickersResponse> updateStickers(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Valid @ModelAttribute StickerDto.StickerUpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        StickerDto.AllStickersResponse updated = stickerService.updateStickers(diaryBookId, request, userDetails);
        return ResponseEntity.ok(updated);
    }
}
