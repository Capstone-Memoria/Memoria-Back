package ac.mju.memoria.backend.domain.invitation.controller;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookMemberDto;
import ac.mju.memoria.backend.domain.invitation.dto.InvitationDto;
import ac.mju.memoria.backend.domain.invitation.service.InvitationService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InvitationController {
    private final InvitationService invitationService;

    @PostMapping("/diaries/{diaryBookId}/invitation/by-code")
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationDto.CreateCodeInviteResponse inviteByCode(
            @PathVariable Long diaryBookId,
            @RequestBody InvitationDto.CreateCodeInviteRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        return invitationService.inviteByCode(request, diaryBookId, user);
    }

    @PostMapping("/invitation/accept/by-code")
    public DiaryBookMemberDto.MemberResponse acceptByCode(
            @RequestBody @Validated InvitationDto.AcceptCodeInviteRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        return invitationService.acceptByCode(request, user);
    }

    @PostMapping("/diaries/{diaryBookId}/invitation/by-direct")
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationDto.CreateDirectInviteResponse inviteByDirect(
            @PathVariable Long diaryBookId,
            @RequestBody @Validated InvitationDto.CreateDirectInviteRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        return invitationService.inviteByDirect(request, diaryBookId, user);
    }

    @PostMapping("/invitation/accept/by-direct")
    public DiaryBookMemberDto.MemberResponse acceptByDirect(
            @RequestBody @Validated InvitationDto.AcceptDirectInviteRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        return invitationService.acceptByDirect(request, user);
    }

}
