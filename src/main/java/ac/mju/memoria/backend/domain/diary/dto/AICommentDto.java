package ac.mju.memoria.backend.domain.diary.dto;

import ac.mju.memoria.backend.domain.diary.entity.AIComment;
import ac.mju.memoria.backend.domain.diarybook.dto.AICharacterDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class AICommentDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "AI 댓글 응답 DTO")
    public static class AICommentResponse {
        @Schema(description = "AI 댓글 ID", example = "1")
        private Long id;
        @Schema(description = "AI 댓글 제목", example = "오늘 일기 좋았어요!")
        private String title;
        @Schema(description = "AI 댓글 내용", example = "일기 잘 읽었습니다. 오늘 하루는 어떠셨나요?")
        private String content;
        @Schema(description = "AI 댓글 작성 캐릭터")
        private AICharacterDto.AICharacterResponse createdBy;
        @Schema(description = "생성 시각")
        private LocalDateTime createdAt;

        public static AICommentResponse from(AIComment aiComment) {
            return AICommentResponse.builder()
                    .id(aiComment.getId())
                    .title(aiComment.getTitle())
                    .content(aiComment.getContent())
                    .createdBy(AICharacterDto.AICharacterResponse.from(aiComment.getCreatedBy()))
                    .createdAt(aiComment.getCreatedAt())
                    .build();
        }
    }
} 