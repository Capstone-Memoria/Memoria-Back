package ac.mju.memoria.backend.domain.diarybook.service;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBookMember;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookMemberRepository;
import ac.mju.memoria.backend.domain.diarybook.repository.DiaryBookQueryRepository;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
