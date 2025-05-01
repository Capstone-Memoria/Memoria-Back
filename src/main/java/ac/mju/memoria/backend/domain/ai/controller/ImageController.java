package ac.mju.memoria.backend.domain.ai.controller;

import ac.mju.memoria.backend.domain.ai.dto.ImageDto;
import ac.mju.memoria.backend.domain.ai.service.ImageCreateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/cover-image")
@RequiredArgsConstructor
@Tag(name = "Image", description = "AI 이미지 생성 API")
public class ImageController {
    private final ImageCreateService imageCreateService;

    @PostMapping
    @Operation(summary = "다이어리 북 커버 이미지 생성", description = "키워드를 기반으로 다이어리 북 커버 이미지를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "이미지 생성 성공 (Base64 인코딩된 문자열 반환)")
    public ResponseEntity<String> createImage(@RequestBody ImageDto.CreateRequest request) {
        String base64Image = imageCreateService.generateImage(request);
        return ResponseEntity.ok(base64Image);
    }
}