package plub.plubserver.domain.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.notification.dto.NotificationDto.NotificationListResponse;
import plub.plubserver.domain.notification.dto.NotificationDto.NotifyParams;
import plub.plubserver.domain.notification.model.NotificationType;
import plub.plubserver.domain.notification.service.FcmService;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.report.service.ReportService;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.report.dto.ReportDto.CreateReportRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    private final FcmService fcmService;
    private final ReportService reportService;
    private final AccountService accountService;
    private final NotificationService notificationService;

    @PostMapping
    public ApiResponse<?> testAuthCode(@RequestBody TestDto.AuthCodeRequest authCodeDto) {
        int statusCode = 200;
        if (!authCodeDto.isLoginSuccess()) {
            statusCode = 400;
             
        }
        TestDto.AuthCodeResponse authCodeResponse = new TestDto.AuthCodeResponse(authCodeDto.authCode());
        return success(authCodeResponse);
    }

    @PostMapping("/report")
    public ApiResponse<?> testReport(@RequestBody CreateReportRequest request) {
        log.info("test");
        Account currentAccount = accountService.getCurrentAccount();
        return success(reportService.createReport(request, currentAccount));
    }

    // for test
    @PostMapping("/push/self")
    public ApiResponse<NotificationListResponse> testCreateNotificationMyself() {
        Account currentAccount = accountService.getCurrentAccount();
        NotifyParams params = NotifyParams.builder()
                .receiver(currentAccount)
                .type(NotificationType.TEST_ACCOUNT_ITSELF)
                .redirectTargetId(currentAccount.getId())
                .title("test")
                .content("자기 자신의 아이디를 리턴")
                .build();
        notificationService.pushMessageForceSave(params);
        return success(notificationService.getMyNotifications(currentAccount));
    }
}
