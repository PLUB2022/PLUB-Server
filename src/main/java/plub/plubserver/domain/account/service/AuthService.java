package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.config.jwt.JwtDto;
import plub.plubserver.config.jwt.JwtProvider;
import plub.plubserver.config.jwt.RefreshToken;
import plub.plubserver.config.jwt.RefreshTokenRepository;
import plub.plubserver.config.security.PrincipalDetails;
import plub.plubserver.domain.account.config.AccountCode;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;

import java.util.Optional;

import static plub.plubserver.config.security.SecurityUtils.getCurrentAccountEmail;
import static plub.plubserver.domain.account.dto.AuthDto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AppleService appleService;
    private final GoogleService googleService;
    private final KakaoService kakaoService;

    public AuthMessage loginAccess(SocialLoginRequest socialLoginRequest) {
        OAuthIdAndRefreshTokenResponse response = fetchSocialEmail(socialLoginRequest);
        String email = response.id();
        String refreshToken = response.refreshToken();
        Optional<Account> account = accountRepository.findByEmail(email);
        AuthMessage loginMessage;
        if (account.isPresent()) {
            JwtDto jwtDto = login(account.get().toAccountRequestDto().toLoginRequest());
            account.get().updateRefreshToken(refreshToken);
            loginMessage = new AuthMessage(
                    AccountCode.LOGIN.getStatusCode(),
                    jwtDto,
                    AccountCode.LOGIN.getMessage()
            );
        } else {
            String signToken = jwtProvider.createSignToken(email, refreshToken);
            loginMessage = new AuthMessage(
                    AccountCode.NEED_TO_SIGNUP.getStatusCode(),
                    signToken,
                    AccountCode.NEED_TO_SIGNUP.getMessage()
            );
        }
        return loginMessage;
    }


    public JwtDto login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = loginRequest.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Account account = principal.getAccount();
        return jwtProvider.issue(account);
    }

    @Transactional
    public SignAuthMessage signUp(SignUpRequest signUpRequest, String header) {
        String signToken = jwtProvider.resolveSignToken(header);
        if (!jwtProvider.validate(signToken))
            throw new AccountException(AccountCode.SIGNUP_TOKEN_ERROR);

        SigningAccount signKey = jwtProvider.getSignKey(signToken);
        String email = signKey.email();
        String socialType = signKey.socialType();
        String refreshToken = signKey.refreshToken();
        String nickname = signUpRequest.nickname();

        duplicateEmailAndNickName(email, nickname);
        Account account = signUpRequest.toAccount(email, socialType, passwordEncoder);
        accountRepository.save(account);
        JwtDto jwtDto = login(account.toAccountRequestDto().toLoginRequest());
        account.updateRefreshToken(refreshToken);

        return new SignAuthMessage(
                AccountCode.SIGNUP_COMPLETE.getStatusCode(),
                jwtDto,
                AccountCode.SIGNUP_COMPLETE.getMessage()
        );
    }

    private void duplicateEmailAndNickName(String email, String nickname) {
        if (accountRepository.existsByEmail(email)) {
            throw new AccountException(AccountCode.EMAIL_DUPLICATION);
        }
        if (accountRepository.existsByNickname(nickname)) {
            throw new AccountException(AccountCode.NICKNAME_DUPLICATION);
        }
    }

    private OAuthIdAndRefreshTokenResponse fetchSocialEmail(SocialLoginRequest socialLoginRequest){
        String provider = socialLoginRequest.socialType();
        if (provider.equalsIgnoreCase("Google")) {
            return googleService.requestGoogleToken(socialLoginRequest.authorizationCode());
        } else if (provider.equalsIgnoreCase("Kakao")) {
            return kakaoService.getKakaoId(socialLoginRequest.accessToken());
        } else {
            return appleService.requestAppleToken(socialLoginRequest);
        }
    }

    public JwtDto reissue(ReissueRequest reissueDto) {
        return jwtProvider.reissue(reissueDto.refreshToken());
    }

    @Transactional
    public String logout() {
        Account account = accountRepository
                .findByEmail(getCurrentAccountEmail())
                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));
        RefreshToken refreshToken = refreshTokenRepository
                .findByAccount(account)
                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_REFRESH_TOKEN));
        refreshTokenRepository.delete(refreshToken);
        refreshTokenRepository.flush();
        return "로그아웃 완료";
    }
}
