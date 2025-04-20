package ac.mju.memoria.backend.domain.diary.controller;

import ac.mju.memoria.backend.domain.diary.dto.ReactionDto;
import ac.mju.memoria.backend.domain.diary.service.ReactionService;
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
@RequestMapping("api/diary-book/{diaryBookId}/diary/{diaryId}/reaction")
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping
    public ResponseEntity<ReactionDto.Response> addReaction(
            @PathVariable Long diaryBookId,
            @PathVariable Long diaryId,
            @RequestBody @Valid ReactionDto.Request request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ReactionDto.Response response = reactionService.addReaction(diaryId, request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    public ResponseEntity<ReactionDto.Response> updateReaction(
            @PathVariable Long diaryBookId,
            @PathVariable Long diaryId,
            @RequestBody @Valid ReactionDto.Request request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ReactionDto.Response response = reactionService.updateReaction(diaryId, request, userDetails);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteReaction(
            @PathVariable Long diaryBookId,
            @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        reactionService.deleteReaction(diaryId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ReactionDto.Response>> getReactions(
            @PathVariable Long diaryBookId,
            @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<ReactionDto.Response> reactions = reactionService.getReactionsForDiary(diaryId, userDetails);
        return ResponseEntity.ok(reactions);
    }
}
