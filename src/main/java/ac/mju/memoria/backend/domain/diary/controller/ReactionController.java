package ac.mju.memoria.backend.domain.diary.controller;

import java.util.List;

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
public class ReactionController {

    private final ReactionService reactionService;

    @PutMapping
    public ResponseEntity<ReactionDto.Response> reactToDiary(
            @PathVariable Long diaryBookId,
            @PathVariable Long diaryId,
            @RequestBody @Valid ReactionDto.Request request,
            @AuthenticationPrincipal UserDetails userDetails) {

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
    public ResponseEntity<List<ReactionDto.Response>> getReactions(
            @PathVariable Long diaryBookId,
            @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<ReactionDto.Response> reactions = reactionService.getReactionsForDiary(diaryId, userDetails);
        return ResponseEntity.ok(reactions);
    }
}
