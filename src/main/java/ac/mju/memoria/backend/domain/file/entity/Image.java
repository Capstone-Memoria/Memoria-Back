package ac.mju.memoria.backend.domain.file.entity;

import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Entity
@Getter @Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Image extends AttachedFile{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    public static Image from(MultipartFile file, Diary diary) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return Image.builder()
                .id(UUID.randomUUID().toString())
                .fileName(file.getOriginalFilename())
                .size(file.getSize())
                .fileType(FileType.IMAGE)
                .diary(diary)
                .build();
    }
}
