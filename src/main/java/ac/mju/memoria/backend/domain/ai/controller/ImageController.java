package ac.mju.memoria.backend.domain.ai.controller;

import ac.mju.memoria.backend.domain.ai.dto.ImageDto;
import ac.mju.memoria.backend.domain.ai.service.ImageCreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/cover-image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageCreateService imageCreateService;

    @PostMapping
    public ResponseEntity<String> createImage(@RequestBody ImageDto.CreateRequest request) {
        String base64Image = imageCreateService.generateImage(request);
        return ResponseEntity.ok(base64Image);
    }
}