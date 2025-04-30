package ac.mju.memoria.backend.domain.diary.controller;

import ac.mju.memoria.backend.domain.diary.dto.CommentDto;
import ac.mju.memoria.backend.domain.diary.service.CommentService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book/{diaryBookId}/diary/{diaryId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto.CommentResponse> createComment(
            @PathVariable Long diaryBookId, @PathVariable Long diaryId,
            @RequestBody @Valid CommentDto.UserCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(diaryBookId, diaryId, request, userDetails));
    }

    @PostMapping("/{commentId}")
    public ResponseEntity<CommentDto.CommentResponse> createReply(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentDto.UserCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createReply(commentId, request, userDetails));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto.CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentDto.UserCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(commentService.updateComment(commentId, request, userDetails));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto.TreeResponse>> getCommentsByDiary(
            @PathVariable Long diaryBookId, @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(commentService.getCommentsByDiary(diaryBookId, diaryId, userDetails));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userdetails) {

        commentService.deleteComment(commentId, userdetails);
        return ResponseEntity.noContent().build();
    }
}
