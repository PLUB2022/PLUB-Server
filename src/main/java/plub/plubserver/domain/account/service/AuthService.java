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
import plub.plubserver.config.jwt.RefreshToken;
import plub.plubserver.config.jwt.RefreshTokenRepository;
import plub.plubserver.config.security.PrincipalDetails;
import plub.plubserver.domain.account.dto.AppleDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.SocialType;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.exception.account.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

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
    private final RestTemplate restTemplate;
    private final AccountRepository accountRepository;
    private final AppleService appleService;

    public AuthMessage loginAccess(SocialLoginRequest socialLoginRequest) {
        String email = fetchSocialEmail(socialLoginRequest);
        Optional<Account> account = accountRepository.findByEmail(email);
        AuthMessage loginMessage;

        if (account.isPresent()) {
            JwtDto jwtDto = login(account.get().toAccountRequestDto().toLoginRequest());
            loginMessage = new AuthMessage(jwtDto, "로그인 완료. 토큰 발행");
        } else {
            String signToken = jwtProvider.createSignToken(email);
            loginMessage = new AuthMessage(signToken, "신규 가입 필요");
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
        SigningAccount signKey = jwtProvider.getSignKey(signToken);
        String email = signKey.email();
        String socialType = signKey.socialType();
        String nickname = signUpRequest.nickname();
        duplicateEmailAndNickName(email, nickname);
        Account account = signUpRequest.toAccount(email, socialType, passwordEncoder);
        accountRepository.save(account);
        JwtDto jwtDto = login(account.toAccountRequestDto().toLoginRequest());
        return new SignAuthMessage(jwtDto, "회원가입 완료. 토큰 발행");
    }

    private void duplicateEmailAndNickName(String email, String nickname) {
        if (accountRepository.existsByEmail(email)) {
            throw new EmailDuplicateException();
        }
        if (accountRepository.existsByNickname(nickname)) {
            throw new NickNameDuplicateException();
        }
    }

    private String fetchSocialEmail(SocialLoginRequest socialLoginRequest){
        String provider = socialLoginRequest.socialType();
        if (provider.equalsIgnoreCase("Google")) {
            return getGoogleId(socialLoginRequest.accessToken());
        } else if (provider.equalsIgnoreCase("Kakao")) {
            return getKakaoId(socialLoginRequest.accessToken());
        } else {
            String appleId = getAppleId(socialLoginRequest.accessToken());
            try {
                appleService.GenerateAuthToken(socialLoginRequest.authorizationCode());
            } catch (Exception e) {
                throw new AppleException();
            }
            return appleId;
        }
    }

    private String getGoogleId(String accessToken) {
        String socialUrl = SocialType.GOOGLE.getSocialUrl();
        HttpMethod httpMethod = SocialType.GOOGLE.getHttpMethod();
        ResponseEntity<Map<String, Object>> response = validAccessToken(accessToken, socialUrl, httpMethod);
        String sub = (String) Objects.requireNonNull(response.getBody()).get("sub");
        return sub+"@GOOGLE";
    }

    private String getKakaoId(String accessToken) {
        String socialUrl = SocialType.KAKAO.getSocialUrl();
        HttpMethod httpMethod = SocialType.KAKAO.getHttpMethod();
        ResponseEntity<Map<String, Object>> response = validAccessToken(accessToken, socialUrl, httpMethod);
        Map<String, Object> googlePK = (Map) response.getBody();
        String id = String.valueOf(googlePK.get("id"));
        return id+"@KAKAO";
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
            String subject = claims.getSubject();
            return subject+"@APPLE";
        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeySpecException | SignatureException |
                MalformedJwtException | ExpiredJwtException | IllegalArgumentException e) {
            throw new AppleException();
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

    @Transactional
    public String logout() {
        Account account = accountRepository.findByEmail(getCurrentAccountEmail())
                .orElseThrow(NotFoundAccountException::new);
        RefreshToken refreshToken = refreshTokenRepository.findByAccount(account)
                .orElseThrow(NotFountRefreshTokenException::new);
        refreshTokenRepository.delete(refreshToken);
        refreshTokenRepository.flush();
        return "로그아웃 완료";
    }
}
