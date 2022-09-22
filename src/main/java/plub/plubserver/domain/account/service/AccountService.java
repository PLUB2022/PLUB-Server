package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import plub.plubserver.domain.account.dto.LoginRequest;
import plub.plubserver.domain.account.dto.SignUpRequest;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.SocialType;
import plub.plubserver.domain.account.repository.AccountRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {


    private final RestTemplate restTemplate;
    private final AccountRepository accountRepository;

    public String loginAccess(LoginRequest loginDto) {
        // 1. access token 검증
        String email = fetchSocialEmail(loginDto);

        // 2. repository 확인
        Optional<Account> account = accountRepository.findByEmail(email);

        // 3. 있으면 로그인 진행 없으면 회원가입
        if (account.isPresent()) {
            // login(account);
            // 앱한테 로그인 됐다고 메시지 전달
        } else {
            // 앱한테 신규가입이라고 메시지 전달
        }

        return email;
    }

    private void login(Account account) {
        // 리프레시토큰 값 생성
    }

    @Transactional
    public String signUp(SignUpRequest signUpDto) {
        String email = signUpDto.getEmail();
        String nickname = signUpDto.getNickname();
        duplicateEmailAndNickName(email, nickname);
        Account account = signUpDto.toAccount();
        accountRepository.save(account);
        // login(account)
        return "회원가입 완료";
    }

    private void duplicateEmailAndNickName(String email, String nickname) {
        if (accountRepository.existsByEmail(email)) {
            // 예외처리
        }
        if (accountRepository.existsByNickname(nickname)) {
            // 예외처리
        }
    }

    private String fetchSocialEmail(LoginRequest loginRequestDto) {
        String provider = loginRequestDto.getProvider();
        if (provider.equalsIgnoreCase("Google")) {
            return getGoogleId(loginRequestDto.getAccessToken());
        } else if (provider.equalsIgnoreCase("Kakao")) {
            return getKakaoId(loginRequestDto.getAccessToken());
        } else {
            return "getAppleId";
        }
    }

    private String getGoogleId(String accessToken) {
        String socialUrl = SocialType.GOOGLE.getSocialUrl();
        HttpMethod httpMethod = SocialType.GOOGLE.getHttpMethod();
        ResponseEntity<Map<String, Object>> response = validAccessToken(accessToken, socialUrl, httpMethod);
        String googlePK = (String) Objects.requireNonNull(response.getBody()).get("email");
        return googlePK;
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
}
