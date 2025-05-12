package ac.mju.memoria.backend.domain.diarybook.entity;

import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter @Setter
@SuperBuilder
@NoArgsConstructor
public class Sticker {
    @Id @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String stickerType;
    private int posX;
    private int posY;
    private int width;
    private int height;
    private int rotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diaryBook_id")
    private DiaryBook diaryBook;
}
