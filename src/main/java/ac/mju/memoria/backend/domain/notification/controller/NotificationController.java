package ac.mju.memoria.backend.domain.notification.controller;

import ac.mju.memoria.backend.domain.notification.dto.NotificationDto;
import ac.mju.memoria.backend.domain.notification.service.NotificationService;
import ac.mju.memoria.backend.system.security.model.UserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 관리 API")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "내 알림 목록 조회", description = "사용자의 모든 알림을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공")
    public ResponseEntity<List<NotificationDto.Response>> getMyNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<NotificationDto.Response> notifications = notificationService.getMyNotifications(userDetails);
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "특정 알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공")
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "알림 ID") @PathVariable Long notificationId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        notificationService.markAsRead(notificationId, userDetails);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    @Operation(summary = "특정 알림 삭제", description = "특정 알림을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "알림 삭제 성공")
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "알림 ID") @PathVariable Long notificationId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    ) {
        notificationService.deleteNotification(notificationId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "모든 알림 삭제", description = "모든 알림을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "모든 알림 삭제 성공")
    public ResponseEntity<Void> deleteAllNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.deleteAllNotifications(userDetails);
        return ResponseEntity.noContent().build();
    }
}
