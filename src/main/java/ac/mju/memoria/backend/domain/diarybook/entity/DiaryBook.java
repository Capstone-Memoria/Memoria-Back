package ac.mju.memoria.backend.domain.diarybook.entity;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.file.entity.CoverImageFile;
import ac.mju.memoria.backend.domain.file.entity.Sticker;
import ac.mju.memoria.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DiaryBook extends UserStampedEntity {
    @Id @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private boolean isPinned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "coverImageFile_id")
    private CoverImageFile coverImageFile;

    //@OneToMany(mappedBy = "diaryBook")
    //private List<Sticker> stickers = new ArrayList<>();
}
