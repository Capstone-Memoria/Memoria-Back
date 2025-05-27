package ac.mju.memoria.backend.domain.diarybook.entity;

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
public class PredefinedSticker extends Sticker {

    private String assetName;
}