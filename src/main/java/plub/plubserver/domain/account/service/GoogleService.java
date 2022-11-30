package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import plub.plubserver.domain.account.config.GoogleProperties;
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

    private final RestTemplate restTemplate;
    private final GoogleProperties googleProperties;
    private final SocialService socialService;

    private String getGoogleId(String accessToken) {
        String socialUrl = SocialType.GOOGLE.getSocialUrl();
        HttpMethod httpMethod = SocialType.GOOGLE.getHttpMethod();
        ResponseEntity<Map<String, Object>> response = socialService.validAccessToken(accessToken, socialUrl, httpMethod);
        String sub = (String) Objects.requireNonNull(response.getBody()).get("sub");
        return sub + "@GOOGLE";
    }

    public OAuthIdAndRefreshTokenResponse requestGoogleToken(final String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = generateTokenParams(code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        GoogleTokenResponse response = fetchGoogleToken(request).getBody();
        String googleId = getGoogleId(response.access_token());
        String refreshToken = response.refresh_token();
        return OAuthIdAndRefreshTokenResponse.to(googleId, refreshToken);
    }

    private MultiValueMap<String, String> generateTokenParams(final String authorizationCode) {
        String code = URLDecoder.decode(authorizationCode, StandardCharsets.UTF_8);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleProperties.clientId);
        params.add("client_secret", googleProperties.clientSecret);
        params.add("code", code);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", googleProperties.redirectUri);
        return params;
    }

    private ResponseEntity<GoogleTokenResponse> fetchGoogleToken(final HttpEntity<MultiValueMap<String, String>> request) {
        try {
            String tokenUrl = "https://oauth2.googleapis.com/token";
            return restTemplate.postForEntity(tokenUrl, request, GoogleTokenResponse.class);
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean revokeGoogle(String refreshToken) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", googleProperties.clientId);
        params.add("client_secret", googleProperties.clientSecret);
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<GoogleRefreshTokenResponse> response = restTemplate.postForEntity(tokenUrl, request, GoogleRefreshTokenResponse.class);
        String accessToken = response.getBody().access_token();
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