package ac.mju.memoria.backend.domain.invitation.service;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookMemberDto;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookMember;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookMemberRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookRepository;
import ac.mju.memoria.backend.domain.invitation.dto.InvitationDto;
import ac.mju.memoria.backend.domain.invitation.entity.CodeInvitation;
import ac.mju.memoria.backend.domain.invitation.entity.DirectInvitation;
import ac.mju.memoria.backend.domain.invitation.entity.Invitation;
import ac.mju.memoria.backend.domain.invitation.repository.CodeInvitationRepository;
import ac.mju.memoria.backend.domain.invitation.repository.DirectInvitationRepository;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.domain.user.repository.UserRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationService {
    private final CodeInvitationRepository codeInvitationRepository;
    private final DiaryBookRepository diaryBookRepository;
    private final DiaryBookMemberRepository diaryBookMemberRepository;
    private final UserRepository userRepository;
    private final DirectInvitationRepository directInvitationRepository;

    @Transactional
    public InvitationDto.CreateCodeInviteResponse inviteByCode(InvitationDto.CreateCodeInviteRequest request, Long diaryBookId, UserDetails user) {
        DiaryBook foundDiaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));

        Optional<CodeInvitation> foundInvitation = codeInvitationRepository.findByDiaryBook(foundDiaryBook);

        int expireHours = Objects.nonNull(request.getHours()) ? request.getHours() : 24;

        CodeInvitation toSave = foundInvitation
                .map(it -> {
                    it.renewInviteCode(expireHours);
                    return it;
                })
                .orElseGet(
                        () -> CodeInvitation.of(foundDiaryBook, user.getUser(), LocalDateTime.now().plusHours(expireHours))
                );

        toSave.canInviteBy(user);
        CodeInvitation saved = codeInvitationRepository.save(toSave);

        return InvitationDto.CreateCodeInviteResponse.from(saved);
    }

    @Transactional
    public InvitationDto.CreateDirectInviteResponse inviteByDirect(InvitationDto.CreateDirectInviteRequest request, Long diaryBookId, UserDetails user) {
        DiaryBook diaryBook = diaryBookRepository.findById(diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        User inviteTo = userRepository.findByEmail(request.getTargetEmail())
                .orElseThrow(() -> new RestException(ErrorCode.AUTH_USER_NOT_FOUND));

        DirectInvitation toSave = DirectInvitation.of(diaryBook, inviteTo, user.getUser());
        toSave.canInviteBy(user);

        DirectInvitation saved = directInvitationRepository.save(toSave);

        return InvitationDto.CreateDirectInviteResponse.from(saved);
    }

    @Transactional
    public DiaryBookMemberDto.MemberResponse acceptByCode(InvitationDto.AcceptCodeInviteRequest request, UserDetails user) {
        CodeInvitation foundInvitation = codeInvitationRepository.findByInviteCode(request.getCode())
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));

        cannotAcceptIfAlreadyMember(user, foundInvitation);

        if(foundInvitation.isExpired()) {
            codeInvitationRepository.delete(foundInvitation);
            throw new RestException(ErrorCode.GLOBAL_NOT_FOUND);
        }

        DiaryBookMember toSave = DiaryBookMember.of(foundInvitation.getDiaryBook(), user.getUser());
        toSave.makeRelationWIthDiaryBook(foundInvitation.getDiaryBook());

        DiaryBookMember saved = diaryBookMemberRepository.save(toSave);
        return DiaryBookMemberDto.MemberResponse.from(saved);
    }

    @Transactional
    public DiaryBookMemberDto.MemberResponse acceptByDirect(InvitationDto.AcceptDirectInviteRequest request, UserDetails user) {
        DirectInvitation foundInvitation = directInvitationRepository.findById(request.getId())
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));

        cannotAcceptIfAlreadyMember(user, foundInvitation);

        DiaryBookMember toSave = DiaryBookMember.of(foundInvitation.getDiaryBook(), user.getUser());
        toSave.makeRelationWIthDiaryBook(foundInvitation.getDiaryBook());

        DiaryBookMember saved = diaryBookMemberRepository.save(toSave);
        return DiaryBookMemberDto.MemberResponse.from(saved);
    }

    private static void cannotAcceptIfAlreadyMember(UserDetails user, Invitation foundInvitation) {
        if(foundInvitation.getDiaryBook().isMember(user.getUser())) {
            throw new RestException(ErrorCode.INVITE_ALREADY_MEMBER);
        }
    }
    @Transactional(readOnly = true)
    public InvitationDto.CodeInviteDetailsResponse getCodeInviteDetails(String code) {
        CodeInvitation invitation = codeInvitationRepository.findByInviteCode(code)
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));

        if (invitation.isExpired()) {
            codeInvitationRepository.delete(invitation);
            throw new RestException(ErrorCode.INVITE_CODE_EXPIRED);
        }

        return InvitationDto.CodeInviteDetailsResponse.from(invitation);
    }

    @Transactional(readOnly = true)
    public List<InvitationDto.ReceivedInvitationResponse> getReceivedInvitations(UserDetails userDetails) {
        User inviteToUser = userDetails.getUser();
        List<DirectInvitation> receivedInvitations = directInvitationRepository.findByInviteTo(inviteToUser);

        return receivedInvitations.stream()
                .map(InvitationDto.ReceivedInvitationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void declineDirectInvite(Long invitationId, UserDetails userDetails) {
        DirectInvitation foundInvitation = directInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));

        // 초대를 받은 본인만 거절 가능
        if (!foundInvitation.getInviteTo().getEmail().equals(userDetails.getUser().getEmail())) {
            throw new RestException(ErrorCode.AUTH_FORBIDDEN);
        }

        directInvitationRepository.delete(foundInvitation);
    }
}

