package plub.plubserver.domain.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.config.jwt.JwtDto;
import plub.plubserver.domain.account.service.AuthService;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.account.dto.AuthDto.*;

@RestController
@RequestMapping("api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService accountService;

    @PostMapping("/login")
    public ApiResponse<AuthMessage> login(@Valid @RequestBody SocialLoginRequest loginDto) {
        return success(accountService.loginAccess(loginDto), "로그인");
    }

    @PostMapping("/signup")
    public ApiResponse<AuthMessage> signUp(@Valid @RequestBody SignUpRequest signUpDto) {
        return success(accountService.signUp(signUpDto), "회원가입");
    }

    @PostMapping("/reissue")
    public ApiResponse<JwtDto> reissue(@RequestBody ReissueRequest reissueDto) {
        return success(accountService.reissue(reissueDto), "JWT 재발급");
    }
}
