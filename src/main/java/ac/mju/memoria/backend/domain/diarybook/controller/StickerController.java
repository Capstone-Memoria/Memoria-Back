package ac.mju.memoria.backend.domain.diarybook.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.mju.memoria.backend.domain.diarybook.dto.StickerDto;
import ac.mju.memoria.backend.domain.diarybook.service.StickerService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Sticker", description = "다이어리 북 스티커 API")
public class StickerController {
    private final StickerService stickerService;

    @Operation(summary = "다이어리 북 스티커 업데이트", description = "특정 다이어리 북에 포함된 스티커 목록을 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스티커 업데이트 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "다이어리 북을 찾을 수 없음", content = @Content)
    })
    @PutMapping("/diary-book/{diaryBookId}/stickers")
    public List<StickerDto.AbstractResponse> updateStickers(
            @Parameter(description = "다이어리 북 ID", required = true, example = "1") @PathVariable Long diaryBookId,
            @Valid @RequestBody StickerDto.UpdateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        return stickerService.updateStickers(diaryBookId, request, userDetails);
    }

    @Operation(summary = "스티커 이미지 임시 보관", description = "커스텀 이미지 스티커에 사용될 이미지를 임시로 업로드하고 보관합니다. 반환된 UUID를 사용하여 스티커 생성/수정 시 활용합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "스티커 이미지 임시 보관 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StickerDto.HoldStickerImageResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값 또는 파일 형식", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping(value = "/stickers/images/hold", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StickerDto.HoldStickerImageResponse holdStickerImage(
            @Parameter(description = "이미지 파일과 함께 요청하는 DTO", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = StickerDto.HoldStickerImageRequest.class))) @Valid @ModelAttribute StickerDto.HoldStickerImageRequest request) {

        return StickerDto.HoldStickerImageResponse.from(stickerService.holdStickerImage(request));
    }
}
