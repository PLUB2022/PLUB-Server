package plub.plubserver.domain.account.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.config.jwt.JwtDto;
import plub.plubserver.domain.account.service.AuthService;

import javax.validation.Valid;

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
    public ApiResponse<?> login(@RequestBody SocialLoginRequest socialLoginRequest) {
        AuthMessage authMessage = authService.loginAccess(socialLoginRequest);
        return success(authMessage.detailData());
    }

    @ApiOperation(value = "소셜 회원가입 및 로그인")
    @PostMapping("/signup")
    public ApiResponse<JwtDto> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        SignAuthMessage signAuthMessage = authService.signUp(signUpRequest);
        return success(signAuthMessage.detailData());
    }

    @ApiOperation(value = "토큰 재발행")
    @PostMapping("/reissue")
    public ApiResponse<JwtDto> reissue(@RequestBody ReissueRequest reissueRequest) {
        return success(authService.reissue(reissueRequest));
    }

    @ApiOperation(value = "로그아웃")
    @GetMapping("/logout")
    public ApiResponse<String> logout() {
        return success(authService.logout());
    }

    @ApiOperation(value = "어드민 로그인")
    @PostMapping("/login/admin")
    public ApiResponse<?> adminLogin(@RequestBody LoginRequest loginRequest) {
        AuthMessage authMessage = authService.loginAdmin(loginRequest);
        return success(authMessage.detailData());
    }
}
