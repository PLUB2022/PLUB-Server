package plub.plubserver.domain.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import plub.plubserver.domain.account.config.AuthCode;
import plub.plubserver.domain.account.dto.AppleDto;
import plub.plubserver.domain.account.exception.AuthException;
import plub.plubserver.domain.account.model.SocialType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static plub.plubserver.domain.account.dto.AppleDto.AppleCodeResponse;
import static plub.plubserver.domain.account.dto.AuthDto.OAuthIdAndRefreshTokenResponse;
import static plub.plubserver.domain.account.dto.AuthDto.SocialLoginRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleService {

    private final RestTemplate restTemplate;

    @Value("${apple.appleBundleId}")
    private String appleBundleId;
    @Value("${apple.appleTeamId}")
    private String appleTeamId;
    @Value("${apple.appleSignKeyId}")
    private Object appleSignKeyId;
    @Value("${apple.appleSignKeyFilePath}")
    private String appleSignKeyFilePath;

    public OAuthIdAndRefreshTokenResponse requestAppleToken(SocialLoginRequest socialLoginRequest) {
        AppleCodeResponse appleAuthToken = generateAuthToken(socialLoginRequest.authorizationCode());
        String appleId = getAppleId(socialLoginRequest.accessToken());
        return OAuthIdAndRefreshTokenResponse.to(appleId, appleAuthToken.refreshToken());
    }

    public AppleCodeResponse generateAuthToken(String authorizationCode) {
        String authUrl = "https://appleid.apple.com/auth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleBundleId);
        params.add("client_secret", createClientSecret());
        params.add("grant_type", "authorization_code");
        params.add("code", authorizationCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<AppleCodeResponse> response = restTemplate.postForEntity(authUrl, httpEntity, AppleCodeResponse.class);
        return response.getBody();
    }

    // Authorization Code로 Token 발급 받기
    public String createClientSecret() {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", appleSignKeyId);
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(appleTeamId)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간 - UNIX 시간
                .setExpiration(expirationDate) // 만료 시간
                .setAudience("https://appleid.apple.com")
                .setSubject(appleBundleId)
                .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                .compact();
    }

    private AppleDto getAppleAuthPublicKey() {
        String socialUrl = SocialType.APPLE.getSocialUrl();
        HttpMethod httpMethod = SocialType.APPLE.getHttpMethod();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<AppleDto> response = restTemplate.exchange(socialUrl, httpMethod, request, AppleDto.class);
        return response.getBody();
    }

    public PrivateKey getPrivateKey() {
        String logTest1 = "getPrivateKey";
        try {
            ClassPathResource resource = new ClassPathResource(appleSignKeyFilePath);
//            ClassPathResource resource = new ClassPathResource("apple_sign_key.p8");

            logTest1 = logTest1 + " 1 " + resource;
            logTest1 = logTest1 + " 10 " + resource.getFilename();
            logTest1 = logTest1 + " 100 " + resource.getURI();

            BufferedReader br = new BufferedReader(new FileReader(resource.getFile()));
            String s = br.readLine();
            System.out.println("s = " + s);
            
            logTest1 = logTest1 + " 1000 " + Arrays.toString(Files.readAllBytes(Paths.get(resource.getURI())));

            String privateKey = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            logTest1 = logTest1 + "2";
            Reader pemReader = new StringReader(privateKey);
            logTest1 = logTest1 + "3";
            PEMParser pemParser = new PEMParser(pemReader);
            logTest1 = logTest1 + "4";
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            logTest1 = logTest1 + "5";
            PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
            logTest1 = logTest1 + "6";
            return converter.getPrivateKey(object);
        } catch (Exception e) {
            throw new AuthException(AuthCode.APPLE_LOGIN_ERROR, e.getMessage()+ logTest1);
        }
    }

    private String getAppleId(String identityToken) {
        String logTest2 = "getAppleId";
        AppleDto appleKeyStorage = getAppleAuthPublicKey();
        try {
            logTest2 = logTest2 + "1";
            String headerToken = identityToken.substring(0, identityToken.indexOf("."));
            logTest2 = logTest2 + "2";
            Map<String, String> header = new ObjectMapper().readValue(new String(Base64.getDecoder().decode(headerToken), StandardCharsets.UTF_8), Map.class);
            logTest2 = logTest2 + "3";
            AppleDto.AppleKey key = appleKeyStorage.getMatchedKeyBy(header.get("kid"), header.get("alg")).orElseThrow();
            logTest2 = logTest2 + "4";
            byte[] nBytes = Base64.getUrlDecoder().decode(key.n());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.e());
            logTest2 = logTest2 + "5";
            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);
            logTest2 = logTest2 + "6";
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            logTest2 = logTest2 + "7";
            KeyFactory keyFactory = KeyFactory.getInstance(key.kty());
            logTest2 = logTest2 + "8";
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            logTest2 = logTest2 + "9";
            Claims claims = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(identityToken).getBody();
            logTest2 = logTest2 + "10";
            String subject = claims.getSubject();
            logTest2 = logTest2 + "11";
            return subject + "@APPLE";
        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeySpecException | SignatureException |
                MalformedJwtException | ExpiredJwtException | IllegalArgumentException e) {
            throw new AuthException(AuthCode.APPLE_LOGIN_ERROR, e.getMessage() + logTest2);
        }
    }


    public boolean revokeApple(String refreshToken) {
        String refreshUrl = "https://appleid.apple.com/auth/token";

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleBundleId);
        params.add("client_secret", createClientSecret());
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<AppleDto.AppleRefreshTokenResponse> response = restTemplate.postForEntity(refreshUrl, httpEntity, AppleDto.AppleRefreshTokenResponse.class);

        String accessToken = Objects.requireNonNull(response.getBody()).accessToken();

        int revokeStatusCode = revoke(accessToken);
        return revokeStatusCode == 200;
    }

    private int revoke(String accessToken) {
        String revokeUrl = "https://appleid.apple.com/auth/revoke";
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appleBundleId);
        params.add("client_secret", createClientSecret());
        params.add("token", accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(revokeUrl, httpEntity, String.class);
        return response.getStatusCode().value();
    }
}