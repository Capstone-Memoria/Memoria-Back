package ac.mju.memoria.backend.domain.file.entity;

import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
//@NoArgsConstructor
@Entity
@Getter
@Setter
@SuperBuilder
public class CoverImageFile extends AttachedFile{
    //TODO: Diary 엔티티와 Relationship 설정

    /*
    @OneToOne(mappedBy = "coverImageFile")
    private Diary diary;
    */

    public static CoverImageFile from(MultipartFile file) {
        return CoverImageFile.builder()
                .fileName(file.getOriginalFilename())
                .size(file.getSize())
                .fileType(FileType.IMAGE)
                .build();
    }
}
