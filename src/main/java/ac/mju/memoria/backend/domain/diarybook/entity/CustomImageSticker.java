package ac.mju.memoria.backend.domain.diarybook.entity;

import ac.mju.memoria.backend.domain.file.entity.StickerImageFile;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "custom_image_sticker")
@DiscriminatorValue("CUSTOM_IMAGE")
public class CustomImageSticker extends Sticker {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "sticker_image_file_id")
    private StickerImageFile imageFile;
}