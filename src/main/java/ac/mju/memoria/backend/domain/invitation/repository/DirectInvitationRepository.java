package ac.mju.memoria.backend.domain.invitation.repository;

import ac.mju.memoria.backend.domain.invitation.entity.DirectInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectInvitationRepository extends JpaRepository<DirectInvitation, Long> {
}
