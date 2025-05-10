package ac.mju.memoria.backend.domain.invitation.repository;

import ac.mju.memoria.backend.domain.invitation.entity.DirectInvitation;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.domain.diarybook.entity.DiaryBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirectInvitationRepository extends JpaRepository<DirectInvitation, Long> {
    List<DirectInvitation> findByInviteTo(User inviteTo);
    boolean existsByDiaryBookAndInviteTo(DiaryBook diaryBook, User inviteTo);
}
