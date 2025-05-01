package ac.mju.memoria.backend.domain.diary.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.mju.memoria.backend.domain.diary.dto.ReactionDto;
import ac.mju.memoria.backend.domain.diary.service.ReactionService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/diary-book/{diaryBookId}/diary/{diaryId}/reaction")
@Tag(name = "Reaction", description = "다이어리 반응 API")
public class ReactionController {

    private final ReactionService reactionService;

    @PutMapping
    @Operation(summary = "다이어리 반응 추가/삭제", description = "특정 다이어리에 반응을 추가하거나 삭제합니다. 동일한 반응 타입으로 다시 요청하면 삭제됩니다.")
    @ApiResponse(responseCode = "200", description = "반응 추가 성공")
    @ApiResponse(responseCode = "204", description = "반응 삭제 성공 (No Content)")
    public ResponseEntity<ReactionDto.Response> reactToDiary(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "다이어리 ID") @PathVariable Long diaryId,
            @RequestBody @Valid ReactionDto.Request request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        ReactionDto.Response response = reactionService.reactToDiary(diaryId, request, userDetails);

        if (response == null) {
            return ResponseEntity
                    .noContent()
                    .build();
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping
    @Operation(summary = "다이어리 반응 목록 조회", description = "특정 다이어리에 달린 모든 반응 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "반응 목록 조회 성공")
    public ResponseEntity<List<ReactionDto.Response>> getReactions(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @Parameter(description = "다이어리 ID") @PathVariable Long diaryId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        List<ReactionDto.Response> reactions = reactionService.getReactionsForDiary(diaryId, userDetails);
        return ResponseEntity.ok(reactions);
    }
}
