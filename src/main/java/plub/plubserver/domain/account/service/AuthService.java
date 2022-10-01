package plub.plubserver.domain.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
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
import plub.plubserver.domain.account.dto.AppleDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.SocialType;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.exception.AccountException;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

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
    private final AppleService appleService;

    public AuthMessage loginAccess(SocialLoginRequest loginDto) {
        String email = fetchSocialEmail(loginDto);
        Optional<Account> account = accountRepository.findByEmail(email);
        AuthMessage loginMessage;

        if (account.isPresent()) {
            JwtDto jwtDto = login(account.get().toAccountRequestDto().toLoginRequest());
            loginMessage = new AuthMessage(jwtDto, "로그인 완료. 토큰 발행");
        } else {
            SigningAccount signingAccount = new SigningAccount(email, loginDto.socialType());
            loginMessage = new AuthMessage(signingAccount,"신규가입 필요");
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
    public SignAuthMessage signUp(SignUpRequest signUpDto) {
        String email = signUpDto.email();
        String nickname = signUpDto.nickname();
        duplicateEmailAndNickName(email, nickname);
        Account account = signUpDto.toAccount(passwordEncoder);
        accountRepository.save(account);
        JwtDto jwtDto = login(account.toAccountRequestDto().toLoginRequest());
        return new SignAuthMessage(jwtDto, "회원가입 완료. 토큰 발행");
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
            return getAppleId(loginRequestDto.accessToken());
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

    private AppleDto getAppleAuthPublicKey(){
        String socialUrl = SocialType.APPLE.getSocialUrl();
        HttpMethod httpMethod = SocialType.APPLE.getHttpMethod();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(headers);
        ResponseEntity<AppleDto> response = restTemplate.exchange(socialUrl, httpMethod, request, AppleDto.class);
        return response.getBody();
    }

    private String getAppleId(String identityToken) {
        AppleDto appleKeyStorage = getAppleAuthPublicKey();
        try {
            String headerToken = identityToken.substring(0,identityToken.indexOf("."));
            Map<String, String> header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerToken), StandardCharsets.UTF_8), Map.class);
            AppleDto.AppleKey key = appleKeyStorage.getMatchedKeyBy(header.get("kid"), header.get("alg")).orElseThrow();

            byte[] nBytes = Base64.getUrlDecoder().decode(key.n());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.e());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.kty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            Claims claims = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(identityToken).getBody();
            return claims.getSubject();
        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeySpecException | SignatureException |
                MalformedJwtException | ExpiredJwtException | IllegalArgumentException e) {
            // 예외처리
            e.printStackTrace();
            throw new RuntimeException();
        }
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

    public void loginAccessWithRevoke(LoginRequest loginRequest) {
        // 나중에 추가
    }

    public AuthMessage revoke(RevokeAccount revokeAccount) throws IOException {

        String email = revokeAccount.email();
        Optional<Account> account = accountRepository.findByEmail(email);

        if (revokeAccount.socialType().equalsIgnoreCase("Google")) {

        } else if (revokeAccount.socialType().equalsIgnoreCase("Kakao")) {

        } else {
            // apple 한 번 로그인 후 authorization_code 가져오기
            // apple 연결 해제
            appleService.revokeApple(account.get(), "authorization_code");
            // 삭제
        }
        return new AuthMessage("d", "탈퇴완료");
    }
}