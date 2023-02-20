package plub.plubserver.domain.test;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.notification.dto.FcmDto;
import plub.plubserver.domain.notification.service.FcmService;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestController {
    private final FcmService fcmService;

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
    public ApiResponse<?> testPushSingle(@RequestBody FcmDto.PushMessage form) {
        return success(fcmService.sendPushMessage(form.targetToken(), form.title(), form.body()));
    }
}
