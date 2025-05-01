package ac.mju.memoria.backend.domain.diary.dto;

import java.time.LocalDateTime;

import ac.mju.memoria.backend.domain.diary.entity.Reaction;
import ac.mju.memoria.backend.domain.diary.entity.ReactionId;
import ac.mju.memoria.backend.domain.diary.entity.enums.ReactionType;
import ac.mju.memoria.backend.domain.user.dto.UserDto.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReactionDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "다이어리 반응 요청 DTO")
    public static class Request {
        @Schema(description = "반응 타입", example = "LIKE")
        private ReactionType reactionType;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "반응 복합 키 응답 DTO")
    public static class IdResponse {
        @Schema(description = "반응한 사용자 정보")
        private UserResponse user;
        @Schema(description = "반응이 달린 다이어리 ID")
        private Long diaryId;

        public static IdResponse from(ReactionId reactionId) {
            return IdResponse.builder()
                    .user(UserResponse.from(reactionId.getUser()))
                    .diaryId(reactionId.getDiary().getId())
                    .build();
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "다이어리 반응 응답 DTO")
    public static class Response {
        @Schema(description = "반응 복합 키 정보")
        private IdResponse reactionId;
        @Schema(description = "반응 타입")
        private ReactionType reactionType;
        @Schema(description = "반응 생성 시간")
        private LocalDateTime createdAt;
        @Schema(description = "반응 마지막 수정 시간")
        private LocalDateTime lastModifiedAt;

        public static Response from(Reaction reaction) {
            return Response.builder()
                    .reactionId(IdResponse.from(reaction.getId()))
                    .reactionType(reaction.getType())
                    .createdAt(reaction.getCreatedAt())
                    .lastModifiedAt(reaction.getLastModifiedAt())
                    .build();
        }
    }
}
