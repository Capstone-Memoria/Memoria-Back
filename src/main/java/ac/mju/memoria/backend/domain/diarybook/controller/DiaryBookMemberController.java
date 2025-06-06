package ac.mju.memoria.backend.domain.diarybook.controller;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookMemberDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import ac.mju.memoria.backend.domain.diarybook.service.DiaryBookMemberService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary-book/{id}/members")
@Tag(name = "DiaryBook Member", description = "다이어리 북 멤버 관리 API")
public class DiaryBookMemberController {
    private final DiaryBookMemberService diaryBookMemberService;

    @DeleteMapping("/{memberId}")
    @Operation(summary = "다이어리 북 멤버 삭제 (추방)", description = "다이어리 북 소유자가 특정 멤버를 추방합니다.")
    @ApiResponse(responseCode = "204", description = "멤버 삭제 성공")
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long id,
            @Parameter(description = "추방할 멤버의 사용자 ID") @PathVariable Long memberId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {

        diaryBookMemberService.deleteMember(id, memberId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "다이어리 북 멤버 목록 조회", description = "특정 다이어리 북에 참여하고 있는 멤버 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "멤버 목록 조회 성공")
    public ResponseEntity<List<DiaryBookMemberDto.MemberResponse>> getMembers(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(diaryBookMemberService.getMembers(id, userDetails));
    }

    @PostMapping("/add-admin")
    @Operation(summary = "다이어리 북 관리자 추가", description = "다이어리 북 관리자가 특정 멤버를 관리자로 추가합니다.")
    @ApiResponse(responseCode = "200", description = "관리자 추가 성공")
    public ResponseEntity<DiaryBookMemberDto.MemberResponse> addAdmin(
            @Parameter(description = "다이어리 북 Id") @PathVariable Long id,
            @Valid @RequestBody DiaryBookMemberDto.ChangeAdminRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(diaryBookMemberService.addAdmin(id, userDetails, request));
    }

    @PostMapping("/remove-admin")
    @Operation(summary = "다이어리 북 관리자 강등", description = "다이어리 북 소유자가 특정 관리자를 멤버로 강등시킵니다.")
    @ApiResponse(responseCode = "200", description = "관리자 강등 성공")
    public ResponseEntity<DiaryBookMemberDto.MemberResponse> removeAdmin(
            @Parameter(description = "다이어리 북 Id") @PathVariable Long id,
            @Valid @RequestBody DiaryBookMemberDto.RemoveAdminRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(diaryBookMemberService.removeAdmin(id, userDetails, request));
    }
}
