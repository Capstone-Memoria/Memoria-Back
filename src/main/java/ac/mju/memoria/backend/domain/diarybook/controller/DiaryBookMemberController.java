package ac.mju.memoria.backend.domain.diarybook.controller;

import ac.mju.memoria.backend.domain.diarybook.service.DiaryBookMemberService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book/{id}/members/{memberId}")
public class DiaryBookMemberController {
    private final DiaryBookMemberService diaryBookMemberService;

    @DeleteMapping
    public ResponseEntity<Void> deleteMember (
            @PathVariable Long id, @PathVariable Long memberId,
            @AuthenticationPrincipal UserDetails userDetails) {

        diaryBookMemberService.deleteMember(id, memberId, userDetails);
        return ResponseEntity.noContent().build();
    }
}
