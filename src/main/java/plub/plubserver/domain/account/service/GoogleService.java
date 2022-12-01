package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import plub.plubserver.domain.account.model.SocialType;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import static plub.plubserver.domain.account.dto.AuthDto.OAuthIdAndRefreshTokenResponse;
import static plub.plubserver.domain.account.dto.GoogleDto.GoogleRefreshTokenResponse;
import static plub.plubserver.domain.account.dto.GoogleDto.GoogleTokenResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleService {

    @Value("${google.client-id}")
    private String clientId;
    @Value("${google.client_secret}")
    private String clientSecret;
    @Value("${google.redirect_uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;
    private final SocialService socialService;

    private String getGoogleId(String accessToken) {
        String socialUrl = SocialType.GOOGLE.getSocialUrl();
        HttpMethod httpMethod = SocialType.GOOGLE.getHttpMethod();
        ResponseEntity<Map<String, Object>> response = socialService.validAccessToken(accessToken, socialUrl, httpMethod);
        String sub = (String) Objects.requireNonNull(response.getBody()).get("sub");
        return sub + "@GOOGLE";
    }

    public OAuthIdAndRefreshTokenResponse requestGoogleToken(final String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = generateTokenParams(code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<GoogleTokenResponse> response = restTemplate.postForEntity(tokenUrl, request, GoogleTokenResponse.class);
        GoogleTokenResponse responseBody = response.getBody();
        String googleId = getGoogleId(responseBody.accessToken());
        String refreshToken = responseBody.refreshToken();
        return OAuthIdAndRefreshTokenResponse.to(googleId, refreshToken);
    }

    private MultiValueMap<String, String> generateTokenParams(final String authorizationCode) {
        String code = URLDecoder.decode(authorizationCode, StandardCharsets.UTF_8);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUri);
        return params;
    }


    protected boolean revokeGoogle(String refreshToken) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<GoogleRefreshTokenResponse> response = restTemplate.postForEntity(tokenUrl, request, GoogleRefreshTokenResponse.class);
        String accessToken = response.getBody().accessToken();
        int revokeStatusCode = revoke(accessToken);
        return revokeStatusCode == 200;
    }

    private int revoke(String accessToken) {
        String revokeUrl = "https://oauth2.googleapis.com/revoke";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("token", accessToken);
        ResponseEntity<String> response = restTemplate.postForEntity(revokeUrl, parameters, String.class);
        return response.getStatusCode().value();
    }
}