package ac.mju.memoria.backend.domain.file.entity;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter @Setter
@SuperBuilder
@NoArgsConstructor
public class Sticker extends AttachedFile {
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "diaryBook_id")
//    private DiaryBook diaryBook;
}
