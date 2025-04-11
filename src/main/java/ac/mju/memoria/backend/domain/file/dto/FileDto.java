package ac.mju.memoria.backend.domain.file.dto;

import ac.mju.memoria.backend.domain.file.entity.AttachedFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class FileDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class FileResponse {
        private String id;
        private String fileName;
        private Long size;

        public static FileResponse from(AttachedFile file) {
            return FileResponse.builder()
                    .id(file.getId())
                    .fileName(file.getFileName())
                    .size(file.getSize())
                    .build();
        }
    }
}
