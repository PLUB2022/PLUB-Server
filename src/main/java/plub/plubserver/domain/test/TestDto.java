package plub.plubserver.domain.test;

import io.swagger.annotations.ApiModelProperty;

public class TestDto {

    public record AuthCodeRequest(
            @ApiModelProperty(value = "통신 테스트용 authCode", example = "AAAAAAAAAAA")
            String authCode,
            @ApiModelProperty(value = "통신 테스트용 로그인 성공여부", example = "true/false")
            boolean isLoginSuccess
    ) {}

    public record AuthCodeResponse(
            String authCode
    ) {}

    public record JsonTestRequest(
            String testMessage
    ) {}

}
