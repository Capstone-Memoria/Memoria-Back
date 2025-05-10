package ac.mju.memoria.backend.domain.notification.repository;

import ac.mju.memoria.backend.domain.notification.entity.Notification;
import ac.mju.memoria.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    void deleteAllByRecipient(User recipient);
}
