package plub.plubserver.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.notification.dto.NotificationDto.*;
import plub.plubserver.domain.notification.service.NotificationService;

import static plub.plubserver.common.dto.ApiResponse.success;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/direct")
    public ApiResponse<ReceivedAccountIdResponse> sendDirect(
            @RequestBody DirectPushRequest directPushRequest
    ) {
        return success(notificationService.sendDirect(directPushRequest));
    }

    @PostMapping("/plubbings")
    public ApiResponse<ReceivedAccountsResponse> sendToPlubbingAccounts(
            @RequestBody PlubbingPushRequest plubbingPushRequest
    ) {
        return success(notificationService.sendToPlubbing(plubbingPushRequest));
    }

    @GetMapping("/accounts/me")
    public ApiResponse<NotificationListResponse> getMyNotifications() {
        return success(notificationService.getMyNotifications());
    }
}
