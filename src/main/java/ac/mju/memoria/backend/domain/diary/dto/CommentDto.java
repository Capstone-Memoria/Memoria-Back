package ac.mju.memoria.backend.domain.diary.dto;

import ac.mju.memoria.backend.domain.diary.entity.Comment;
import ac.mju.memoria.backend.domain.diary.entity.UserComment;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommentDto {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserCommentRequest {
        @NotBlank(message = "내용을 입력하세요.")
        private String content;

        public UserComment toEntity() {
            return UserComment.builder()
                    .content(content)
                    .isDeleted(false)
                    .build();
        }
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentResponse {
        private Long id;
        private String content;
        private boolean isDeleted;
        private DiaryDto.DiaryResponse diary;
        private UserDto.UserResponse createdBy;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private UserDto.UserResponse lastModifiedBy;
        private Long parentId;

        public static CommentResponse from(Comment comment) {
            return CommentResponse.builder()
                    .id(comment.getId())
                    .content(!comment.isDeleted() ? comment.getContent() : null)
                    .parentId(Optional.ofNullable(comment.getParent()).map(Comment::getId).orElse(null))
                    .isDeleted(comment.isDeleted())
                    .diary(DiaryDto.DiaryResponse.fromEntity(comment.getDiary()))
                    .createdBy(UserDto.UserResponse.from(comment.getUser()))
                    .createdAt(comment.getCreatedAt())
                    .lastModifiedAt(comment.getLastModifiedAt())
                    .lastModifiedBy(UserDto.UserResponse.from(comment.getLastModifiedBy()))
                    .build();
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeResponse extends CommentResponse {
        List<TreeResponse> children = new ArrayList<>();

        public static TreeResponse from(Comment comment) {
            return TreeResponse.builder()
                    .id(comment.getId())
                    .content(!comment.isDeleted() ? comment.getContent() : null)
                    .parentId(Optional.ofNullable(comment.getParent()).map(Comment::getId).orElse(null))
                    .isDeleted(comment.isDeleted())
                    .diary(DiaryDto.DiaryResponse.fromEntity(comment.getDiary()))
                    .createdBy(UserDto.UserResponse.from(comment.getUser()))
                    .children(comment.getChildren().stream().map(TreeResponse::from).toList())
                    .createdAt(comment.getCreatedAt())
                    .lastModifiedAt(comment.getLastModifiedAt())
                    .lastModifiedBy(UserDto.UserResponse.from(comment.getLastModifiedBy()))
                    .build();
        }
    }
}
