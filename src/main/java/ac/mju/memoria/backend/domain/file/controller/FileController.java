package ac.mju.memoria.backend.domain.file.controller;

import ac.mju.memoria.backend.domain.file.service.AttachedFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final AttachedFileService attachedFileService;

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileId
    ) throws IOException {
        Resource resource = attachedFileService.getResourceById(fileId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Disposition", "attachment; filename=\"" + fileId + "\"")
                .header("Content-Type", "application/octet-stream")
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/image/{fileId}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String fileId
    ) {
        Resource resource = attachedFileService.getResourceById(fileId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "image/jpeg")
                .body(resource);
    }
}

