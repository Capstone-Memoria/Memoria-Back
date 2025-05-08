package ac.mju.memoria.backend.domain.diarybook.entity;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.AICharacterType;
import ac.mju.memoria.backend.domain.file.entity.ProfileImage;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profileImage_id")
    private ProfileImage profileImage;

    private String name;
    
    @Enumerated(EnumType.STRING)
    private AICharacterType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private DiaryBook diaryBook;

    private String feature;

    private String accent;
}
