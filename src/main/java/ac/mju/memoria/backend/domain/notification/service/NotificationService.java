package ac.mju.memoria.backend.domain.notification.service;

import ac.mju.memoria.backend.domain.notification.dto.NotificationDto;
import ac.mju.memoria.backend.domain.notification.entity.Notification;
import ac.mju.memoria.backend.domain.notification.repository.NotificationRepository;
import ac.mju.memoria.backend.domain.user.entity.User;
import ac.mju.memoria.backend.system.exception.model.ErrorCode;
import ac.mju.memoria.backend.system.exception.model.RestException;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationDto.Response> getMyNotifications(UserDetails userDetails) {
        User user = userDetails.getUser();
        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);

        return notifications.stream()
                .map(NotificationDto.Response::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId, UserDetails userDetails) {
        User user = userDetails.getUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));

        notification.setRead(true);
    }

    @Transactional
    public void deleteNotification(Long notificationId, UserDetails userDetails) {
        User user = userDetails.getUser();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RestException(ErrorCode.GLOBAL_NOT_FOUND));

        notificationRepository.deleteById(notificationId);
    }

    @Transactional
    public void deleteAllNotifications(UserDetails userDetails) {
        User user = userDetails.getUser();
        notificationRepository.deleteAllByRecipient(user);
    }
}
