package ac.mju.memoria.backend.domain.file.entity;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.file.entity.enums.FileType;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import jakarta.persistence.*;
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
@NoArgsConstructor
public class Sticker extends AttachedFile {
    @Enumerated(EnumType.STRING)
    private StickerType stickerType;
    private int posX;
    private int posY;
    private int width;
    private int height;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "diaryBook_id")
    private DiaryBook diaryBook;
}
