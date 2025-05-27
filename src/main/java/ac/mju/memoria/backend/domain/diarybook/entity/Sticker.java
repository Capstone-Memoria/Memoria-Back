package ac.mju.memoria.backend.domain.diarybook.entity;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.file.entity.enums.StickerType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sticker")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "sticker_discriminator_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Sticker extends UserStampedEntity {

    @Id
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_book_id")
    private DiaryBook diaryBook;

    private Double posX;

    private Double posY;

    private Double size;

    private Integer rotation;

    @Enumerated(EnumType.STRING)
    private StickerType stickerType;
}