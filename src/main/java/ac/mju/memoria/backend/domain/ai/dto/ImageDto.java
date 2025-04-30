package ac.mju.memoria.backend.domain.ai.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ImageDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class CreateRequest {
        private String description;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class InternalCreateRequest {
        private String prompt;
        private String negativePrompt;
    }
}
