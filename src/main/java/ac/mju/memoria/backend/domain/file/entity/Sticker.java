package ac.mju.memoria.backend.domain.file.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Sticker extends AttachedFile {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coverImageFile_id")
    private CoverImageFile coverImageFile;
}
