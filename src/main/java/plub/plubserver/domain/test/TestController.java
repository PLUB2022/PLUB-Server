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
import plub.plubserver.domain.notification.dto.FcmDto.PushMessage;
import plub.plubserver.domain.notification.service.FcmService;
import plub.plubserver.domain.report.service.ReportService;
import plub.plubserver.domain.test.TestDto.JsonTestRequest;

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

    @PostMapping
    public ApiResponse<?> testAuthCode(@RequestBody TestDto.AuthCodeRequest authCodeDto) {
        int statusCode = 200;
        if (!authCodeDto.isLoginSuccess()) {
            statusCode = 400;
             
        }
        TestDto.AuthCodeResponse authCodeResponse = new TestDto.AuthCodeResponse(authCodeDto.authCode());
        return success(authCodeResponse);
    }

    @PostMapping("/push/single")
    public ApiResponse<?> testPushSingle(@RequestBody PushMessage form) {
        Account targetAccount = accountService.getAccount(form.receiverId());
        return success(fcmService.sendPushMessage(targetAccount.getFcmToken(), form.title(), form.body()));
    }

    @PostMapping("/report")
    public ApiResponse<?> testReport(@RequestBody CreateReportRequest request) {
        log.info("test");
        Account currentAccount = accountService.getCurrentAccount();
        return success(reportService.createReport(request, currentAccount));
    }

    @PostMapping("/json")
    public ApiResponse<String> jsonStringTest(@RequestBody JsonTestRequest request) {
        System.out.println(request);
        return success(request.testMessage());
    }
}
