package plub.plubserver.domain.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.account.dto.AuthDto;
import plub.plubserver.domain.account.service.AccountService;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequestMapping("api/v1/auth/")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/login")
    public ApiResponse<String> login(@Valid @RequestBody AuthDto.SocialLoginRequest loginDto) {
        return success(accountService.loginAccess(loginDto), "로그인");
    }

    @PostMapping("/signup")
    public ApiResponse<String> signUp(@Valid @RequestBody AuthDto.SignUpRequest signUpDto) {
        return success(accountService.signUp(signUpDto), "회원가입");
    }
}
