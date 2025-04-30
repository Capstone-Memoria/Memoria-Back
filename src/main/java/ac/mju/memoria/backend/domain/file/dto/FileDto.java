package ac.mju.memoria.backend.domain.file.dto;

import ac.mju.memoria.backend.domain.file.entity.AttachedFile;
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
    public static class FileResponse {
        private String id;
        private String fileName;
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
