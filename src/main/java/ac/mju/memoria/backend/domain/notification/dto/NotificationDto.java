package ac.mju.memoria.backend.domain.notification.dto;

import ac.mju.memoria.backend.domain.notification.entity.Notification;
import ac.mju.memoria.backend.domain.notification.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NotificationDto {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private NotificationType notificationType;
        private String message;
        private boolean isRead;
        private String url;
        private LocalDateTime createdAt;

        public static Response from(Notification notification) {
            return Response.builder()
                    .id(notification.getId())
                    .notificationType(notification.getNotificationType())
                    .message(notification.getMessage())
                    .isRead(notification.isRead())
                    .url(notification.getUrl())
                    .createdAt(notification.getCreatedAt())
                    .build();
        }
    }
}
