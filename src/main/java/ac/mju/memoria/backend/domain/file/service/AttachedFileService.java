package ac.mju.memoria.backend.domain.file.service;

import ac.mju.memoria.backend.domain.file.entity.AttachedFile;
import ac.mju.memoria.backend.domain.file.handler.FileSystemHandler;
import ac.mju.memoria.backend.domain.file.repository.AttachedFileRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttachedFileService {
    private final AttachedFileRepository attachedFileRepository;
    private final FileSystemHandler fileSystemHandler;

    @Transactional(readOnly = true)
    public Resource getResourceById(String fileId) {
        AttachedFile found = attachedFileRepository.findById(fileId)
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));

        return fileSystemHandler.loadFileAsResource(found);
    }
}
