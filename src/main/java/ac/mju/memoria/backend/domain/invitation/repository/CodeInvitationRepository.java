package ac.mju.memoria.backend.domain.invitation.repository;

import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import ac.mju.memoria.backend.domain.invitation.entity.CodeInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CodeInvitationRepository extends JpaRepository<CodeInvitation, Long> {
    Optional<CodeInvitation> findByDiaryBook(DiaryBook diaryBook);

    Optional<CodeInvitation> findByInviteCode(String inviteCode);
}
