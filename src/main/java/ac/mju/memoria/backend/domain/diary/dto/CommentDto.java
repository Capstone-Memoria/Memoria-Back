package ac.mju.memoria.backend.domain.diary.dto;

import ac.mju.memoria.backend.domain.diary.entity.Comment;
import ac.mju.memoria.backend.domain.diary.entity.UserComment;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "댓글/대댓글 생성 및 수정 요청 DTO")
    public static class UserCommentRequest {
        @NotBlank(message = "내용을 입력하세요.")
        @Schema(description = "댓글 내용", example = "정말 멋진 하루였네요!")
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
    @Schema(description = "댓글 응답 DTO")
    public static class CommentResponse {
        @Schema(description = "댓글 ID")
        private Long id;
        @Schema(description = "댓글 내용 (삭제된 경우 null)")
        private String content;
        @Schema(description = "삭제 여부")
        private boolean isDeleted;
        @Schema(description = "댓글이 달린 다이어리 정보")
        private DiaryDto.DiaryResponse diary;
        @Schema(description = "댓글 작성자 정보")
        private UserDto.UserResponse createdBy;
        @Schema(description = "댓글 생성 시간")
        private LocalDateTime createdAt;
        @Schema(description = "댓글 마지막 수정 시간")
        private LocalDateTime lastModifiedAt;
        @Schema(description = "댓글 마지막 수정자 정보")
        private UserDto.UserResponse lastModifiedBy;
        @Schema(description = "부모 댓글 ID (대댓글이 아닌 경우 null)")
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
    @Schema(description = "계층 구조 댓글 응답 DTO")
    public static class TreeResponse extends CommentResponse {
        @Schema(description = "대댓글 목록")
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
