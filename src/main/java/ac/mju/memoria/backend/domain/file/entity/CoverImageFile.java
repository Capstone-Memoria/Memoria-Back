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

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@SuperBuilder
public class CoverImageFile extends AttachedFile{
//    @OneToOne(fetch = FetchType.LAZY, mappedBy = "coverImageFile")
//    private DiaryBook diaryBook;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "coverImageFile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sticker> stickers = new ArrayList<>();

    public void addSticker(Sticker sticker) {
        this.stickers.add(sticker);
        sticker.setCoverImageFile(this);
    }

    public static CoverImageFile from(MultipartFile file) {
        return CoverImageFile.builder()
                .fileName(file.getOriginalFilename())
                .size(file.getSize())
                .fileType(FileType.IMAGE)
                .build();
    }
}
