package ac.mju.memoria.backend.domain.diary.dto;

import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiaryRequestDto {
  @NotBlank(message = "제목은 필수 입력값입니다.")
  private String title;

  @NotBlank(message = "내용은 필수 입력값입니다.")
  private String content;

  @NotNull(message = "일기장 ID는 필수 입력값입니다.")
  private Long diaryBookId;

  public Diary toEntity(DiaryBook diaryBook) {
    return Diary.builder()
        .title(title)
        .content(content)
        .diaryBook(diaryBook)
        .build();
  }
}