package ac.mju.memoria.backend.domain.file.dto;

import ac.mju.memoria.backend.domain.file.entity.AttachedFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

public class FileDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @Schema(description = "파일 응답 DTO")
    public static class FileResponse {
        @Schema(description = "파일 ID (UUID)", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")
        private String id;
        @Schema(description = "원본 파일 이름", example = "image.jpg")
        private String fileName;
        @Schema(description = "파일 크기 (bytes)", example = "102400")
        private Long size;

        public static FileResponse from(AttachedFile file) {
            if(Objects.isNull(file)) {
                return null;
            }

            return FileResponse.builder()
                    .id(file.getId())
                    .fileName(file.getFileName())
                    .size(file.getSize())
                    .build();
        }
    }
}
