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
@Table(name = "custom_text_sticker")
@DiscriminatorValue("CUSTOM_TEXT")
public class CustomTextSticker extends AbstractSticker {
    private Integer templateWidth;
    private String textContent;
    private Integer fontSize;
    private String fontFamily;
    private String fontColor;
    private Boolean italic;
    private Boolean bold;
}