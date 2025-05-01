package ac.mju.memoria.backend.domain.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ImageDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @Schema(description = "AI 이미지 생성 요청 DTO")
    public static class CreateRequest {
        @Schema(description = "이미지 생성을 위한 키워드", example = "우주를 여행하는 고양이")
        private String description;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    // 내부 로직에서 사용되므로 Swagger 문서에는 포함하지 않음
    public static class InternalCreateRequest {
        private String prompt;
        private String negativePrompt;
    }
}
