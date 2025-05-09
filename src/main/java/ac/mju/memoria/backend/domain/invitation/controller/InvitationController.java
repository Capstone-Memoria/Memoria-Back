package ac.mju.memoria.backend.domain.invitation.controller;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookMemberDto;
import ac.mju.memoria.backend.domain.invitation.dto.InvitationDto;
import ac.mju.memoria.backend.domain.invitation.service.InvitationService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Invitation", description = "초대 API")
public class InvitationController {
    private final InvitationService invitationService;

    @PostMapping("/diaries/{diaryBookId}/invitation/by-code")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "초대 코드 생성", description = "특정 다이어리 북에 대한 초대 코드를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "초대 코드 생성 성공")
    public InvitationDto.CreateCodeInviteResponse inviteByCode(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @RequestBody InvitationDto.CreateCodeInviteRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user
    ) {
        return invitationService.inviteByCode(request, diaryBookId, user);
    }

    @PostMapping("/invitation/accept/by-code")
    @Operation(summary = "초대 코드로 참여", description = "초대 코드를 사용하여 다이어리 북에 참여합니다.")
    @ApiResponse(responseCode = "200", description = "초대 수락 성공")
    public DiaryBookMemberDto.MemberResponse acceptByCode(
            @RequestBody @Validated InvitationDto.AcceptCodeInviteRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user
    ) {
        return invitationService.acceptByCode(request, user);
    }

    @PostMapping("/diaries/{diaryBookId}/invitation/by-direct")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "직접 초대 생성", description = "특정 사용자를 다이어리 북에 직접 초대합니다.")
    @ApiResponse(responseCode = "201", description = "직접 초대 생성 성공")
    public InvitationDto.CreateDirectInviteResponse inviteByDirect(
            @Parameter(description = "다이어리 북 ID") @PathVariable Long diaryBookId,
            @RequestBody @Validated InvitationDto.CreateDirectInviteRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user
    ) {
        return invitationService.inviteByDirect(request, diaryBookId, user);
    }

    @PostMapping("/invitation/accept/by-direct")
    @Operation(summary = "직접 초대 수락", description = "받은 직접 초대를 수락하여 다이어리 북에 참여합니다.")
    @ApiResponse(responseCode = "200", description = "초대 수락 성공")
    public DiaryBookMemberDto.MemberResponse acceptByDirect(
            @RequestBody @Validated InvitationDto.AcceptDirectInviteRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user
    ) {
        return invitationService.acceptByDirect(request, user);
    }

    @GetMapping("/invitation/by-code/{code}")
    @Operation(summary = "초대 코드 정보 조회", description = "초대 코드에 해당하는 다이어리 북 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "초대 정보 조회 성공")
    public InvitationDto.CodeInviteDetailsResponse getCodeInviteDetails(
            @Parameter(description = "초대 코드") @PathVariable String code
    ) {
        return invitationService.getCodeInviteDetails(code);
    }

    @GetMapping("/invitations/received")
    @Operation(summary = "받은 초대 목록 조회", description = "인증된 사용자가 받은 직접 초대 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "받은 초대 목록 조회 성공")
    public List<InvitationDto.ReceivedInvitationResponse> getReceivedInvitations(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user
    ) {
        return invitationService.getReceivedInvitations(user);
    }

    @DeleteMapping("/invitations/{invitationId}")
    @Operation(summary = "초대 거절", description = "받은 직접 초대를 거절합니다.")
    @ApiResponse(responseCode = "200", description = "초대 거절 성공")
    @ApiResponse(responseCode = "404", description = "초대를 찾을 수 없음")
    @ApiResponse(responseCode = "403", description = "초대를 거절할 권한이 없음")
    public void declineDirectInvite(
            @Parameter(description = "초대 ID") @PathVariable Long invitationId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails user
    ) {
        invitationService.declineDirectInvite(invitationId, user);
    }
}
