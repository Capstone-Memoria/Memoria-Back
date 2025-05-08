package ac.mju.memoria.backend.domain.file.entity;

import ac.mju.memoria.backend.domain.diary.entity.Diary;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MusicFile extends AttachedFile {
  @OneToOne
  @JoinColumn(name = "diary_id")
  private Diary diary;
}