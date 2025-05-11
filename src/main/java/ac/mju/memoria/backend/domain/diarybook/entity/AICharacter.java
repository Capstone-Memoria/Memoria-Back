package ac.mju.memoria.backend.domain.diarybook.entity;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.AICharacterType;
import ac.mju.memoria.backend.domain.file.entity.ProfileImage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AICharacter extends UserStampedEntity {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "aiCharacter")
    private ProfileImage profileImage;

    private String name;
    
    @Enumerated(EnumType.STRING)
    private AICharacterType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private DiaryBook diaryBook;

    @Column(length = 1000)
    private String feature;

    @Column(length = 1000)
    private String accent;
}
