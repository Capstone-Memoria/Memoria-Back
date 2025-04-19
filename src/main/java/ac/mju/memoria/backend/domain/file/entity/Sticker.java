package ac.mju.memoria.backend.domain.file.entity;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter @Setter
@SuperBuilder
@NoArgsConstructor
public class Sticker {
    @Id
    private String id;

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
