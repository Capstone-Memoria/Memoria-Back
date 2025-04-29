package ac.mju.memoria.backend.domain.comment.dto;

import ac.mju.memoria.backend.domain.comment.entity.Comment;
import ac.mju.memoria.backend.domain.comment.entity.UserComment;
import ac.mju.memoria.backend.domain.diary.dto.DiaryDto;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

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
        private Long parentId;

        public static CommentResponse from(Comment comment) {
            return CommentResponse.builder()
                    .id(comment.getId())
                    .content(!comment.isDeleted() ? comment.getContent() : null)
                    .parentId(Optional.ofNullable(comment.getParent()).map(Comment::getId).orElse(null))
                    .isDeleted(comment.isDeleted())
                    .diary(DiaryDto.DiaryResponse.fromEntity(comment.getDiary()))
                    .createdBy(UserDto.UserResponse.from(comment.getCreatedBy()))
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
                    .createdBy(UserDto.UserResponse.from(comment.getCreatedBy()))
                    .children(comment.getChildren().stream().map(TreeResponse::from).toList())
                    .build();
        }
    }
}
