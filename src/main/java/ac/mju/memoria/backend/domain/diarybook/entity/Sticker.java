package ac.mju.memoria.backend.domain.diarybook.entity;

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
    private String uuid;

    private String stickerType;

    private Double posX;

    private Double posY;

    private Double size;

    private Integer rotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diaryBook_id")
    private DiaryBook diaryBook;
}
