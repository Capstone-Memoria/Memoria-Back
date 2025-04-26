package ac.mju.memoria.backend.domain.diary.dto;

import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DiaryDto {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiaryRequest {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        private List<MultipartFile> images;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DiaryUpdateRequest {
        private String title;
        private String content;
        private List<String> toDeleteImageIds;
        private List<MultipartFile> toAddImages;

        public Diary applyTo(DiaryUpdateRequest request, Diary diary) {
            if (Objects.nonNull(request.getTitle())) {
                diary.setTitle(request.getTitle());
            }

            if (Objects.nonNull(request.getContent())) {
                diary.setContent(request.getContent());
            }

            return diary;
        }
    }


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
        private List<FileDto.FileResponse> images;
        private List<ReactionDto.Response> reactions;

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
                    .images(diary.getImages() == null ? Collections.emptyList() :
                            diary.getImages().stream().
                                    map(FileDto.FileResponse::from)
                                    .collect(Collectors.toList()))
                    .reactions(diary.getReactions() == null? Collections.emptyList() :
                            diary.getReactions().stream()
                                    .map(ReactionDto.Response::from)
                                    .collect(Collectors.toList()))
                    .build();
        }
    }
}
