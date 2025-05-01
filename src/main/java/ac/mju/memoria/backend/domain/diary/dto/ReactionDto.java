package ac.mju.memoria.backend.domain.diary.dto;

import java.time.LocalDateTime;

import ac.mju.memoria.backend.domain.diary.entity.Reaction;
import ac.mju.memoria.backend.domain.diary.entity.ReactionId;
import ac.mju.memoria.backend.domain.diary.entity.enums.ReactionType;
import ac.mju.memoria.backend.domain.user.dto.UserDto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ReactionDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private ReactionType reactionType;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IdResponse {
        private UserResponse user;
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
    public static class Response {
        private IdResponse reactionId;
        private ReactionType reactionType;
        private LocalDateTime createdAt;
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
