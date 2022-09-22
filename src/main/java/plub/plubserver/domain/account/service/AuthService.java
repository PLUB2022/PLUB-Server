package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import plub.plubserver.config.jwt.JwtDto;
import plub.plubserver.config.jwt.JwtProvider;
import plub.plubserver.config.jwt.RefreshTokenRepository;
import plub.plubserver.config.security.PrincipalDetails;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.SocialType;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.exception.AccountException;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static plub.plubserver.domain.account.dto.AuthDto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    private final AccountRepository accountRepository;

    public AuthMessage loginAccess(SocialLoginRequest loginDto) {
        // 1. access token 검증
        String email = fetchSocialEmail(loginDto);

        // 2. repository 확인
        Optional<Account> account = accountRepository.findByEmail(email);

        AuthMessage loginMessage;

        // 3. 있으면 로그인 진행 없으면 회원가입
        if (account.isPresent()) {
//            JwtDto jwtDto = login(account.get().toAccountRequestDto().toLoginRequest(account.get().getPassword()));
            JwtDto jwtDto = login(account.get().toAccountRequestDto().toLoginRequest());
            loginMessage = new AuthMessage(jwtDto, "로그인");
        } else {
            SigningAccount signingAccount = new SigningAccount(email, loginDto.socialType());
            loginMessage = new AuthMessage(signingAccount,"신규가입");
        }
        return loginMessage;
    }


    private JwtDto login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = loginRequest.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Account account = principal.getAccount();
        return jwtProvider.issue(account);
    }

    @Transactional
    public AuthMessage signUp(SignUpRequest signUpDto) {
        String email = signUpDto.email();
        String nickname = signUpDto.nickname();
        duplicateEmailAndNickName(email, nickname);
        Account account = signUpDto.toAccount(passwordEncoder);
        accountRepository.save(account);
        JwtDto jwtDto = login(account.toAccountRequestDto().toLoginRequest());
        return new AuthMessage(jwtDto, "회원가입 완료");
    }

    private void duplicateEmailAndNickName(String email, String nickname) {
        if (accountRepository.existsByEmail(email)) {
            throw new AccountException("email 중복 입니다.");
        }
        if (accountRepository.existsByNickname(nickname)) {
            throw new AccountException("nickname 중복 입니다.");
        }
    }

    private String fetchSocialEmail(SocialLoginRequest loginRequestDto) {
        String provider = loginRequestDto.socialType();
        if (provider.equalsIgnoreCase("Google")) {
            return getGoogleId(loginRequestDto.accessToken());
        } else if (provider.equalsIgnoreCase("Kakao")) {
            return getKakaoId(loginRequestDto.accessToken());
        } else {
            return "getAppleId";
        }
    }

    private String getGoogleId(String accessToken) {
        String socialUrl = SocialType.GOOGLE.getSocialUrl();
        HttpMethod httpMethod = SocialType.GOOGLE.getHttpMethod();
        ResponseEntity<Map<String, Object>> response = validAccessToken(accessToken, socialUrl, httpMethod);
        return (String) Objects.requireNonNull(response.getBody()).get("email");
    }

    private String getKakaoId(String accessToken) {
        String socialUrl = SocialType.KAKAO.getSocialUrl();
        HttpMethod httpMethod = SocialType.KAKAO.getHttpMethod();
        ResponseEntity<Map<String, Object>> response = validAccessToken(accessToken, socialUrl, httpMethod);
        Map<String, Object> googlePK = (Map<String, Object>) response.getBody().get("kakao_account");
        return (String) googlePK.get("email");
    }

    private ResponseEntity<Map<String, Object>> validAccessToken(String accessToken, String socialUrl, HttpMethod httpMethod) {
        HttpHeaders headers = setHeaders(accessToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE  =  new ParameterizedTypeReference<>(){};
        return restTemplate.exchange(socialUrl, httpMethod, request, RESPONSE_TYPE);
    }

    public HttpHeaders setHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public JwtDto reissue(ReissueRequest reissueDto) {
        return jwtProvider.reIssue(reissueDto.refreshToken());
    }
}
