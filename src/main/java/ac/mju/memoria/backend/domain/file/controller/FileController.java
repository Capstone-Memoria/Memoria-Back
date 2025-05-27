package ac.mju.memoria.backend.domain.file.controller;

import ac.mju.memoria.backend.domain.file.service.AttachedFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "파일 다운로드/조회 API")
public class FileController {
    private final AttachedFileService attachedFileService;

    @GetMapping("/download/{fileId}")
    @Operation(summary = "파일 다운로드", description = "파일 ID를 사용하여 파일을 다운로드합니다.")
    @ApiResponse(responseCode = "200", description = "파일 다운로드 성공",
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    schema = @Schema(type = "string", format = "binary")))
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "다운로드할 파일 ID (UUID)") @PathVariable String fileId
    ) throws IOException {
        Resource resource = attachedFileService.getResourceById(fileId);

        // 파일 이름을 원본 파일 이름으로 설정 (필요 시 AttachedFileService에서 원본 파일명 조회 로직 추가)
        // String originalFilename = attachedFileService.getOriginalFilenameById(fileId);
        // .header("Content-Disposition", "attachment; filename=\"" + originalFilename + "\"")

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Disposition", "attachment; filename=\"" + fileId + "\"") // 임시로 fileId 사용
                .header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @GetMapping("/image/{fileId}")
    @Operation(summary = "이미지 조회", description = "파일 ID를 사용하여 이미지를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "이미지 조회 성공",
            content = @Content(mediaType = MediaType.IMAGE_JPEG_VALUE, // 또는 PNG 등 실제 이미지 타입에 맞게 설정
                    schema = @Schema(type = "string", format = "binary")))
    public ResponseEntity<Resource> getImage(
            @Parameter(description = "조회할 이미지 파일 ID (UUID)") @PathVariable String fileId
    ) {
        Resource resource = attachedFileService.getResourceById(fileId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Cache-Control", "max-age=3600, must-revalidate") // 캐시 설정 (필요 시 조정)
                .header("Content-Type", MediaType.IMAGE_JPEG_VALUE) // 실제 이미지 타입에 맞게 설정 필요
                .body(resource);
    }
}
