package ac.mju.memoria.backend.domain.diary.dto;

import ac.mju.memoria.backend.domain.diary.entity.Reaction;
import ac.mju.memoria.backend.domain.diary.entity.enums.ReactionType;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

public class ReactionDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        @NotNull(message = "공감 종류를 선택하세요")
        private ReactionType reactionType;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private ReactionType reactionType;
        private LocalDateTime createdAt;
        private UserDto.UserResponse createdBy;
        private LocalDateTime lastModifiedAt;
        private UserDto.UserResponse lastModifiedBy;


        public static Response from(Reaction reaction) {
            if (reaction == null) return null;
            return Response.builder()
                    .id(reaction.getId())
                    .createdAt(reaction.getCreatedAt())
                    .createdBy(UserDto.UserResponse.from(reaction.getCreatedBy()))
                    .reactionType(reaction.getType())
                    .lastModifiedAt(reaction.getLastModifiedAt())
                    .lastModifiedBy(UserDto.UserResponse.from(reaction.getLastModifiedBy()))
                    .build();
        }
    }
}
