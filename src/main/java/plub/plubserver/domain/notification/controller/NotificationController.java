package plub.plubserver.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.notification.dto.NotificationDto.NotificationListResponse;
import plub.plubserver.domain.notification.dto.NotificationDto.NotificationResponse;
import plub.plubserver.domain.notification.service.NotificationService;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/accounts/me")
    public ApiResponse<NotificationListResponse> getMyNotifications() {
        return success(notificationService.getMyNotifications());
    }

    @PutMapping("/{notificationId}/read")
    public ApiResponse<NotificationResponse> readNotification(@PathVariable Long notificationId) {
        return success(notificationService.readNotification(notificationId));
    }


}
