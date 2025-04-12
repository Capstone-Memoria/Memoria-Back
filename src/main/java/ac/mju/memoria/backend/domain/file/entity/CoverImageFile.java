package ac.mju.memoria.backend.domain.file.entity;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class CoverImageFile extends AttachedFile{
//    @OneToOne(fetch = FetchType.LAZY, mappedBy = "coverImageFile")
//    private DiaryBook diaryBook;

    public static CoverImageFile from(MultipartFile file) {
        return CoverImageFile.builder()
                .fileName(file.getOriginalFilename())
                .size(file.getSize())
                .fileType(FileType.IMAGE)
                .build();
    }
}
