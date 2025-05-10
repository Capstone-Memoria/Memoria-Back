package ac.mju.memoria.backend.domain.diary.controller;

import ac.mju.memoria.backend.domain.diary.dto.AICommentDto;
import ac.mju.memoria.backend.domain.diary.entity.AIComment;
import ac.mju.memoria.backend.domain.diary.service.AICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diaries/{diaryId}/ai-comments")
@Tag(name = "AIComment", description = "AI 댓글 API")
public class AICommentController {

    private final AICommentService aiCommentService;

    @GetMapping
    @Operation(summary = "AI 댓글 조회", description = "특정 다이어리의 AI 댓글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "AI 댓글 조회 성공")
    public List<AICommentDto.AICommentResponse> getAICommentsByDiaryId(@Parameter(description = "다이어리 ID") @PathVariable Long diaryId) {
        return aiCommentService.getAICommentsByDiaryId(diaryId);
    }
} 