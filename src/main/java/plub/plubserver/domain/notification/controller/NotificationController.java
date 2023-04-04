package plub.plubserver.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.notification.dto.NotificationDto.NotificationListResponse;
import plub.plubserver.domain.notification.dto.NotificationDto.NotificationResponse;
import plub.plubserver.domain.notification.service.NotificationService;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final AccountService accountService;

    @GetMapping("/accounts/me")
    public ApiResponse<NotificationListResponse> getMyNotifications() {
        Account currentAccount = accountService.getCurrentAccount();
        return success(notificationService.getMyNotifications(currentAccount));
    }

    @PutMapping("/{notificationId}/read")
    public ApiResponse<NotificationResponse> readNotification(@PathVariable Long notificationId) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(notificationService.readNotification(notificationId, currentAccount));
    }


}
