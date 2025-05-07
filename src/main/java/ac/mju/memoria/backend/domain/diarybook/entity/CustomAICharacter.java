package ac.mju.memoria.backend.domain.diarybook.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("CUSTOM")
public class CustomAICharacter extends AICharacter{
    @ManyToOne(fetch = FetchType.LAZY)
    private DiaryBook diaryBook;

    private String feature;

    private String accent;
}
