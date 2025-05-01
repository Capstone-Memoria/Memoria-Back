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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

    @GetMapping("/count")
    public ResponseEntity<Long> getCommentCountByDiary(
            @PathVariable Long diaryBookId, @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(commentService.getCommentCountByDiary(diaryBookId, diaryId, userDetails));
    }
}
