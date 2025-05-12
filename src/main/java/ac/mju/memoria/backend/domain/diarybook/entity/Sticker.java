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
    @Id
    private String uuid;

    private String stickerType;

    private Integer posX;

    private Integer posY;

    private Double scale;

    private Integer rotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diaryBook_id")
    private DiaryBook diaryBook;
}
