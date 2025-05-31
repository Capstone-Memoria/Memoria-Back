package ac.mju.memoria.backend.domain.diarybook.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ac.mju.memoria.backend.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.mju.memoria.backend.domain.diarybook.dto.DiaryBookMemberDto;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookMember;
import ac.mju.memoria.backend.domain.diarybook.entity.enums.MemberPermission;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookMemberRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.domain.user.dto.UserDto;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryBookMemberService {
    private final DiaryBookMemberRepository diaryBookMemberRepository;
    private final DiaryBookQueryRepository diaryBookQueryRepository;

    @Transactional
    public void deleteMember(Long diaryBookId, Long memberId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        DiaryBookMember member = diaryBookMemberRepository.findByIdAndDiaryBookId(memberId, diaryBookId)
                .orElseThrow(() -> new RestException(ErrorCode.MEMBER_NOT_FOUND));

        diaryBook.getMembers().remove(member);
        diaryBookMemberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public List<DiaryBookMemberDto.MemberResponse> getMembers(Long diaryBookId, UserDetails userDetails) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        // 기존 멤버 목록을 DTO로 변환
        List<DiaryBookMemberDto.MemberResponse> members = diaryBook.getMembers().stream()
                .map(DiaryBookMemberDto.MemberResponse::from)
                .collect(Collectors.toCollection(ArrayList::new)); // 수정 가능한 리스트로 생성

        // 오너 정보를 ADMIN 권한으로 추가
        DiaryBookMemberDto.MemberResponse ownerMember = DiaryBookMemberDto.MemberResponse.builder()
                .id(null) // 오너는 DiaryBookMember 엔티티가 아니므로 ID는 null 또는 다른 값으로 설정
                .diaryBookId(diaryBook.getId())
                .user(UserDto.UserResponse.from(diaryBook.getOwner()))
                .permission(MemberPermission.ADMIN)
                .build();

        members.add(ownerMember);

        return members;
    }

    @Transactional
    public DiaryBookMemberDto.MemberResponse addAdmin(Long diaryBookId, UserDetails userDetails, DiaryBookMemberDto.ChangeAdminRequest request) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        User currentAdmin = userDetails.getUser();

        if (!diaryBook.isAdmin(currentAdmin)) {
            throw new RestException(ErrorCode.AUTH_FORBIDDEN);
        }

        DiaryBookMember newAdminMember = diaryBookMemberRepository.findById(request.getNewAdminId())
                .orElseThrow(() -> new RestException(ErrorCode.MEMBER_NOT_FOUND));

        if (newAdminMember.getPermission() == MemberPermission.ADMIN || diaryBook.getOwner().equals(newAdminMember.getUser())) {
            throw new RestException(ErrorCode.MEMBER_ALREADY_ADMIN);
        }

        newAdminMember.setPermission(MemberPermission.ADMIN);

        return DiaryBookMemberDto.MemberResponse.from(newAdminMember);
    }

    @Transactional
    public DiaryBookMemberDto.MemberResponse removeAdmin(Long diaryBookId, UserDetails userDetails, DiaryBookMemberDto.RemoveAdminRequest request) {
        DiaryBook diaryBook = diaryBookQueryRepository.findByIdAndUserEmail(diaryBookId, userDetails.getKey())
                .orElseThrow(() -> new RestException(ErrorCode.DIARYBOOK_NOT_FOUND));

        if (!diaryBook.getOwner().getEmail().equals(userDetails.getKey())) {
            throw new RestException(ErrorCode.AUTH_FORBIDDEN);
        }

        DiaryBookMember toRemoveMember = diaryBookMemberRepository.findById(request.getToRemoveId())
                .orElseThrow(() -> new RestException(ErrorCode.MEMBER_NOT_FOUND));

        if (toRemoveMember.getPermission() != MemberPermission.ADMIN) {
            throw new RestException(ErrorCode.MEMBER_NOT_ADMIN);
        }

        toRemoveMember.setPermission(MemberPermission.MEMBER);

        return DiaryBookMemberDto.MemberResponse.from(toRemoveMember);
    }
}
