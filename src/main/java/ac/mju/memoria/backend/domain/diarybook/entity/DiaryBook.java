package ac.mju.memoria.backend.domain.diarybook.entity;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.file.entity.CoverImageFile;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DiaryBook extends UserStampedEntity {
    @Id @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String title;

    private boolean isPinned;

    /*
    매핑이랑 Dto도 설정해야함
     */

//    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "coverImageFile_id")
//    private CoverImageFile coverImageFile;

}
