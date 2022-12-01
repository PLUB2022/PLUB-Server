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

import java.util.Collections;
import java.util.Map;

import static plub.plubserver.domain.account.dto.AuthDto.OAuthIdAndRefreshTokenResponse;
import static plub.plubserver.domain.account.dto.AuthDto.RevokeKakaoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.appAdminKey}")
    private String appAdminKey;

    private final RestTemplate restTemplate;
    private final SocialService socialService;

    protected OAuthIdAndRefreshTokenResponse getKakaoId(String accessToken) {
        String socialUrl = SocialType.KAKAO.getSocialUrl();
        HttpMethod httpMethod = SocialType.KAKAO.getHttpMethod();
        ResponseEntity<Map<String, Object>> response = socialService.validAccessToken(accessToken, socialUrl, httpMethod);
        Map<String, Object> googlePK = response.getBody();
        String id = String.valueOf(googlePK.get("id"));
        return OAuthIdAndRefreshTokenResponse.to(id + "@KAKAO", " @");
    }

    protected boolean revokeKakao(String userId) {
        String unlinkUrl = "https://kapi.kakao.com/v1/user/unlink";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", userId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + appAdminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<RevokeKakaoResponse> response = restTemplate.postForEntity(unlinkUrl, httpEntity, RevokeKakaoResponse.class);
        int revokeStatusCode = response.getStatusCode().value();
        return revokeStatusCode == 200;
    }
}
