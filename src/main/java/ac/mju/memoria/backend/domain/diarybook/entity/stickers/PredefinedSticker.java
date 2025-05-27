package ac.mju.memoria.backend.domain.diarybook.entity.stickers;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "predefined_sticker")
@DiscriminatorValue("PREDEFINED")
public class PredefinedSticker extends AbstractSticker {
    private String assetName;
}