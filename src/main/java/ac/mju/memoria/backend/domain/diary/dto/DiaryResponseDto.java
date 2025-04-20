package ac.mju.memoria.backend.domain.diary.dto;

import java.time.LocalDateTime;

import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DiaryResponseDto {

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class DiaryResponse {
    private Long id;
    private String title;
    private String content;
    private Long diaryBookId;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private UserDto.UserResponse createdBy;
    private UserDto.UserResponse lastModifiedBy;

    public static DiaryResponse fromEntity(Diary diary) {
      return DiaryResponse.builder()
          .id(diary.getId())
          .title(diary.getTitle())
          .content(diary.getContent())
          .diaryBookId(diary.getDiaryBook().getId())
          .createdAt(diary.getCreatedAt())
          .lastModifiedAt(diary.getLastModifiedAt())
          .createdBy(UserDto.UserResponse.from(diary.getCreatedBy()))
          .lastModifiedBy(UserDto.UserResponse.from(diary.getLastModifiedBy()))
          .build();
    }
  }
}