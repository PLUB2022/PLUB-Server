package plub.plubserver.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.notification.dto.NotificationDto.NotificationListResponse;
import plub.plubserver.domain.notification.dto.NotificationDto.NotificationRequest;
import plub.plubserver.domain.notification.dto.NotificationDto.ReceivedAccountIdResponse;
import plub.plubserver.domain.notification.dto.NotificationDto.TotalReceivedAccountNumResponse;
import plub.plubserver.domain.notification.service.NotificationService;

import static plub.plubserver.common.dto.ApiResponse.success;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ApiResponse<ReceivedAccountIdResponse> send(
            @RequestBody NotificationRequest notificationRequest
    ) {
        return success(notificationService.send(notificationRequest));
    }

    @PostMapping("/accounts")
    public ApiResponse<TotalReceivedAccountNumResponse> sendAll() {
        return success(notificationService.sendAll());
    }

    @GetMapping("/accounts/me")
    public ApiResponse<NotificationListResponse> getMyNotifications() {
        return success(notificationService.getMyNotifications());
    }

    @PutMapping("/accounts/me/check")
    public ApiResponse<String> checkIsRead() {
        return success(notificationService.checkIsRead());
    }
}
