package plub.plubserver.domain.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import plub.plubserver.common.dto.ApiResponse;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
public class TestController {

    @PostMapping("/api/test")
    public ApiResponse<?> testAuthCode(@RequestBody TestDto.AuthCodeRequest authCodeDto) {
        int statusCode = 200;
        if (!authCodeDto.isLoginSuccess()) {
            statusCode = 400;
        }
        TestDto.AuthCodeResponse authCodeResponse = new TestDto.AuthCodeResponse(authCodeDto.authCode());
        return success(
                statusCode,
                authCodeResponse
        );
    }
}
