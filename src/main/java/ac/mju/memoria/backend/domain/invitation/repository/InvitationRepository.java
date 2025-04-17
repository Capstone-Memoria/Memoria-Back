package ac.mju.memoria.backend.domain.invitation.repository;

import ac.mju.memoria.backend.domain.invitation.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
}
