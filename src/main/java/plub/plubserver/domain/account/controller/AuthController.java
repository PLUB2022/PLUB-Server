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

import java.io.IOException;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.account.dto.AuthDto.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody SocialLoginRequest loginDto) throws IOException {
        AuthMessage authMessage = authService.loginAccess(loginDto);
        return success(authMessage.detailData(), authMessage.detailMessage());
    }

    @PostMapping("/signup")
    public ApiResponse<?> signUp(@Valid @RequestBody SignUpRequest signUpDto) {
        SignAuthMessage signAuthMessage = authService.signUp(signUpDto);
        return success(signAuthMessage.detailData(), signAuthMessage.detailMessage());
    }

    @PostMapping("/reissue")
    public ApiResponse<JwtDto> reissue(@RequestBody ReissueRequest reissueDto) {
        return success(authService.reissue(reissueDto), "JWT 재발급");
    }


}
