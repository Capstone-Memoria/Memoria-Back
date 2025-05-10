package ac.mju.memoria.backend.domain.diary.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.mju.memoria.backend.domain.diary.dto.CommentDto;
import ac.mju.memoria.backend.domain.diary.service.CommentService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book/{diaryBookId}/diary/{diaryId}/comments")
@Tag(name = "Comment", description = "댓글 API")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "댓글 생성", description = "특정 다이어리에 새로운 댓글을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "댓글 생성 성공")
    public ResponseEntity<CommentDto.CommentResponse> createComment(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "다이어리 ID") @PathVariable Long diaryId,
            @RequestBody @Valid CommentDto.UserCommentRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(diaryBookId, diaryId, request, userDetails));
    }

    @PostMapping("/{commentId}")
    @Operation(summary = "대댓글 생성", description = "특정 댓글에 대한 대댓글을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "대댓글 생성 성공")
    public ResponseEntity<CommentDto.CommentResponse> createReply(
            @Parameter(description = "부모 댓글 ID") @PathVariable Long commentId,
            @RequestBody @Valid CommentDto.UserCommentRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createReply(commentId, request, userDetails));
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "특정 댓글의 내용을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 수정 성공")
    public ResponseEntity<CommentDto.CommentResponse> updateComment(
            @Parameter(description = "수정할 댓글 ID") @PathVariable Long commentId,
            @RequestBody @Valid CommentDto.UserCommentRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(commentService.updateComment(commentId, request, userDetails));
    }

    @GetMapping
    @Operation(summary = "다이어리 댓글 목록 조회", description = "특정 다이어리의 모든 댓글을 계층 구조로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공")
    public ResponseEntity<List<CommentDto.TreeResponse>> getCommentsByDiary(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "다이어리 ID") @PathVariable Long diaryId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(commentService.getCommentsByDiary(diaryBookId, diaryId, userDetails));
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "댓글 삭제 성공")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "삭제할 댓글 ID") @PathVariable Long commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userdetails) {

        commentService.deleteComment(commentId, userdetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "다이어리 댓글 수 조회", description = "특정 다이어리의 댓글 수를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "댓글 수 조회 성공")
    public ResponseEntity<Long> getCommentCountByDiary(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "다이어리 ID") @PathVariable Long diaryId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(commentService.getCommentCountByDiary(diaryBookId, diaryId, userDetails));
    }
}
