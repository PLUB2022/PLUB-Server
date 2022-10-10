package plub.plubserver.domain.account.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.config.jwt.JwtDto;
import plub.plubserver.domain.account.service.AuthService;

import javax.validation.Valid;
import java.io.IOException;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.account.dto.AuthDto.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Api(tags = "로그인 API")
public class AuthController {

    private final AuthService authService;

    @ApiOperation(value = "소셜 로그인 접근")
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody SocialLoginRequest loginDto) throws IOException {
        AuthMessage authMessage = authService.loginAccess(loginDto);
        return success(authMessage.detailData(), authMessage.detailMessage());
    }

    @ApiOperation(value = "소셜 회원가입 및 로그인")
    @PostMapping("/signup")
    public ApiResponse<JwtDto> signUp(@Valid @RequestBody SignUpRequest signUpDto) {
        SignAuthMessage signAuthMessage = authService.signUp(signUpDto);
        return success(signAuthMessage.detailData(), signAuthMessage.detailMessage());
    }

    @ApiOperation(value = "토큰 재발행")
    @PostMapping("/reissue")
    public ApiResponse<JwtDto> reissue(@RequestBody ReissueRequest reissueDto) {
        return success(authService.reissue(reissueDto), "JWT 재발급");
    }


}
