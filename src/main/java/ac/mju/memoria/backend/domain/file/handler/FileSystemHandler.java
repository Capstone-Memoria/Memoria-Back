package ac.mju.memoria.backend.domain.file.handler;

import ac.mju.memoria.backend.domain.file.entity.AttachedFile;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class FileSystemHandler {
    @Value("${file.save-path}")
    private String savePath;

    @SneakyThrows
    public void saveFile(MultipartFile multipartFile, AttachedFile attachedFile) {
        File targetFile = Paths.get(savePath, attachedFile.getId()).toFile();

        if(targetFile.exists()) {
            throw new RestException(ErrorCode.FILE_ALREADY_EXISTS);
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.flush();
        } catch (Exception e) {
            throw new RestException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @SneakyThrows
    public Resource loadFileAsResource(AttachedFile attachedFile) {
        File targetFile = Paths.get(savePath, attachedFile.getId()).toFile();

        if (!targetFile.exists()) {
            throw new RestException(ErrorCode.FILE_NOT_FOUND);
        }

        return new InputStreamResource(new FileInputStream(targetFile));
    }
}
