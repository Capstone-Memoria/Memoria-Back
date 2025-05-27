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
@Table(name = "custom_text_sticker")
@DiscriminatorValue("CUSTOM_TEXT")
public class CustomTextSticker extends Sticker {

    private String textContent;
    private String fontFamily;
    private String fontSize;
    private String fontColor;
    private String backgroundColor;
}