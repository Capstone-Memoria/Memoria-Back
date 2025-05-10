package ac.mju.memoria.backend.domain.file.handler;

import ac.mju.memoria.backend.domain.file.entity.AttachedFile;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
 import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@RequiredArgsConstructor
public class FileSystemHandler {
    @Value("${file.save-path}")
    private String savePath;

    @SneakyThrows
    public void saveFile(MultipartFile multipartFile, AttachedFile attachedFile) {
        createDirIfNotExist(savePath);

        File targetFile = Paths.get(savePath, attachedFile.getId()).toFile();

        if (targetFile.exists()) {
            throw new RestException(ErrorCode.FILE_ALREADY_EXISTS);
        }

        targetFile.createNewFile();

        try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.flush();
        } catch (Exception e) {
            throw new RestException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @SneakyThrows
    /**
     * 스트림을 파일로 저장하고 파일 크기를 반환합니다.
     * 
     * @param inputStream  저장할 스트림
     * @param attachedFile 저장할 파일 정보
     * @return 저장된 파일의 크기
     */
    public long saveStream(InputStream inputStream, AttachedFile attachedFile) {
        createDirIfNotExist(savePath);

        Path targetPath = Paths.get(savePath, attachedFile.getId());

        if (Files.exists(targetPath)) {
            throw new RestException(ErrorCode.FILE_ALREADY_EXISTS);
        }

        Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        return Files.size(targetPath);
    }

    private void createDirIfNotExist(String path) {
        File targetDir = Paths.get(path).toFile();
        if (targetDir.exists()) {
            return;
        }

        targetDir.mkdir();
    }

    @SneakyThrows
    public Resource loadFileAsResource(AttachedFile attachedFile) {
        File targetFile = Paths.get(savePath, attachedFile.getId()).toFile();

        if (!targetFile.exists()) {
            throw new RestException(ErrorCode.FILE_NOT_FOUND);
        }

        return new FileSystemResource(targetFile);
    }

    @SneakyThrows
    public void deleteFile(AttachedFile attachedFile) {
        File targetFile = Paths.get(savePath, attachedFile.getId()).toFile();

        if (!targetFile.exists()) {
            return;
        }

        if (!targetFile.delete()) {
            throw new RestException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}