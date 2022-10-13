package plub.plubserver.domain.account.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.config.jwt.JwtDto;
import plub.plubserver.config.jwt.JwtProvider;
import plub.plubserver.domain.account.dto.AccountDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.exception.account.EmailDuplicateException;
import plub.plubserver.exception.account.NicknameDuplicateException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static plub.plubserver.domain.account.AccountTemplate.*;
import static plub.plubserver.domain.account.dto.AuthDto.SignAuthMessage;
import static plub.plubserver.domain.account.dto.AuthDto.SignUpRequest;

@SpringBootTest
@WithMockUser(value = EMAIL,password = PASSWORD)
@Transactional
class AuthServiceTest {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AuthService authService;


    @Test
    void signUp_성공() throws IOException {
        // given
        SignUpRequest signUpRequest = makeSignUpRequest();
        // when
        SignAuthMessage signAuthMessage = authService.signUp(signUpRequest);
        String accessToken = signAuthMessage.detailData().accessToken();
        String refreshToken = signAuthMessage.detailData().refreshToken();
        // then
        assertThat(signAuthMessage.detailMessage()).isEqualTo("회원가입 완료");
        assertThat(jwtProvider.validate(accessToken)).isTrue();
        assertThat(jwtProvider.validate(refreshToken)).isTrue();
    }

    @Test
    void signUp_중복가입_이메일() {
        // given
        Account account = makeAccount1();
        accountRepository.save(account);
        SignUpRequest signUpRequest = makeSignUpRequest();
        // when
        // then
        Assertions.assertThrows(EmailDuplicateException.class, () -> authService.signUp(signUpRequest));
    }

    @Test
    void signUp_중복가입_닉네임() {
        // given
        Account account = makeAccount2();
        accountRepository.save(account);
        SignUpRequest signUpRequest = makeSignUpRequest();
        // when
        // then
        Assertions.assertThrows(NicknameDuplicateException.class, () -> authService.signUp(signUpRequest));
    }

    @Test
    void login_성공() {
        // given
        Account account = makeAccount1();
        accountRepository.save(account);
        AccountDto.AccountRequest accountRequestDto = makeLoginRequest1();
        // when
        JwtDto tokenDto = authService.login(accountRequestDto.toLoginRequest());
        // then
        assertThat(tokenDto.accessToken()).isNotNull();
        assertThat(tokenDto.refreshToken()).isNotNull();
    }

    @Test
    void login_실패() {
        // given
        Account account = makeAccount1();
        accountRepository.save(account);
        AccountDto.AccountRequest accountRequestDto = makeLoginRequest2();
        // when
        // then
        Assertions.assertThrows(BadCredentialsException.class, () -> authService.login(accountRequestDto.toLoginRequest()));
    }
}