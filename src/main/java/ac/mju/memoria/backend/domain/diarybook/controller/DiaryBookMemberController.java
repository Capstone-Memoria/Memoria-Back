package ac.mju.memoria.backend.domain.diarybook.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.mju.memoria.backend.domain.diarybook.service.DiaryBookMemberService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book/{id}/members")
public class DiaryBookMemberController {
    private final DiaryBookMemberService diaryBookMemberService;

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable Long id, @PathVariable Long memberId,
            @AuthenticationPrincipal UserDetails userDetails) {

        diaryBookMemberService.deleteMember(id, memberId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getMembers(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(diaryBookMemberService.getMembers(id, userDetails));
    }
}
