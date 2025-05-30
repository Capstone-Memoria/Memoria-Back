package ac.mju.memoria.backend.domain.diary.dto;

import ac.mju.memoria.backend.domain.diary.entity.Diary;
import ac.mju.memoria.backend.domain.diary.entity.enums.EmotionType;
import ac.mju.memoria.backend.domain.file.dto.FileDto;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @Schema(description = "다이어리 생성 요청 DTO")
    public static class DiaryRequest {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Schema(description = "다이어리 제목", example = "오늘의 일기")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        @Schema(description = "다이어리 내용", example = "오늘은 즐거운 하루였다.")
        private String content;

//        @NotNull(message = "감정 상태는 필수 입력값입니다.")
        @Schema(description = "다이어리 감정 상태", example = "HAPPY")
        private EmotionType emotion;

        @Schema(description = "첨부 이미지 파일 목록")
        private List<MultipartFile> images;

        @Schema(description = "답장을 바라는 AI 캐릭터 ID", example = "character_id_123")
        private Long desiredCharacterId;

        @Schema(description = "AI 답장 활성화 여부")
        @NotNull
        private Boolean isAICommentEnabled;

        @Schema(description = "AI 음악 생성 활성화 여부")
        @NotNull
        private Boolean isAIMusicEnabled;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "다이어리 수정 요청 DTO")
    public static class DiaryUpdateRequest {
        @Schema(description = "수정할 다이어리 제목", example = "수정된 오늘의 일기")
        private String title;
        @Schema(description = "수정할 다이어리 내용", example = "오늘은 정말 즐거운 하루였다.")
        private String content;
        @Schema(description = "수정할 다이어리 감정 상태", example = "SAD")
        private EmotionType emotion;
        @Schema(description = "삭제할 이미지 ID 목록", example = "[\"image_id_1\", \"image_id_2\"]")
        private List<String> toDeleteImageIds;
        @Schema(description = "추가할 이미지 파일 목록")
        private List<MultipartFile> toAddImages;

        public Diary applyTo(DiaryUpdateRequest request, Diary diary) {
            if (Objects.nonNull(request.getTitle())) {
                diary.setTitle(request.getTitle());
            }

            if (Objects.nonNull(request.getContent())) {
                diary.setContent(request.getContent());
            }

            if (Objects.nonNull(request.getEmotion())) {
                diary.setEmotion(request.getEmotion());
            }

            return diary;
        }
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "다이어리 응답 DTO")
    public static class DiaryResponse {
        @Schema(description = "다이어리 ID")
        private Long id;
        @Schema(description = "다이어리 제목")
        private String title;
        @Schema(description = "다이어리 내용")
        private String content;
        @Schema(description = "다이어리 감정 상태")
        private EmotionType emotion;
        @Schema(description = "다이어리가 속한 다이어리 북 ID")
        private Long diaryBookId;
        @Schema(description = "다이어리 생성 시간")
        private LocalDateTime createdAt;
        @Schema(description = "다이어리 마지막 수정 시간")
        private LocalDateTime lastModifiedAt;
        @Schema(description = "다이어리 작성자 정보")
        private UserDto.UserResponse createdBy;
        @Schema(description = "다이어리 마지막 수정자 정보")
        private UserDto.UserResponse lastModifiedBy;
        @Schema(description = "첨부된 이미지 파일 정보 목록")
        private List<FileDto.FileResponse> images;
        @Schema(description = "다이어리에 생성된 음악 파일")
        private FileDto.FileResponse musicFile;
        @Schema(description = "댓글 개수")
        private Integer commentCount;
        @Schema(description = "리액션 개수")
        private Integer reactionCount;
        @Schema(description = "AI 음악 생성 활성화 여부")
        private Boolean aiMusicEnabled;

        public static DiaryResponse from(Diary diary) {
            return DiaryResponse.builder()
                    .id(diary.getId())
                    .title(diary.getTitle())
                    .content(diary.getContent())
                    .emotion(diary.getEmotion())
                    .diaryBookId(diary.getDiaryBook().getId())
                    .createdAt(diary.getCreatedAt())
                    .lastModifiedAt(diary.getLastModifiedAt())
                    .createdBy(UserDto.UserResponse.from(diary.getCreatedBy()))
                    .lastModifiedBy(UserDto.UserResponse.from(diary.getLastModifiedBy()))
                    .images(diary.getImages() == null ? Collections.emptyList() :
                            diary.getImages().stream().
                                    map(FileDto.FileResponse::from)
                                    .collect(Collectors.toList()))
                    .musicFile(diary.getMusicFile() == null ? null : FileDto.FileResponse.from(diary.getMusicFile()))
                    .aiMusicEnabled(diary.getAiMusicEnabled())
                    .commentCount(diary.getComments() == null ? 0 : diary.getComments().size())
                    .reactionCount(diary.getReactions() == null ? 0 : diary.getReactions().size())
                    .build();
        }
    }
}
