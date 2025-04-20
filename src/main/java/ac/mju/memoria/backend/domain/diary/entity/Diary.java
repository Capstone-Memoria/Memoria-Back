package ac.mju.memoria.backend.domain.diary.entity;

import ac.mju.memoria.backend.common.auditor.UserStampedEntity;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Diary extends UserStampedEntity {
  @Id
  @Setter(AccessLevel.NONE)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "diary_book_id")
  private DiaryBook diaryBook;
}