package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import plub.plubserver.domain.account.config.KakaoProperties;
import plub.plubserver.domain.account.dto.AuthDto;
import plub.plubserver.domain.account.model.SocialType;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final RestTemplate restTemplate;
    private final SocialService socialService;
    private final KakaoProperties kakaoProperties;

    protected AuthDto.OAuthIdAndRefreshTokenResponse getKakaoId(String accessToken) {
        String socialUrl = SocialType.KAKAO.getSocialUrl();
        HttpMethod httpMethod = SocialType.KAKAO.getHttpMethod();
        ResponseEntity<Map<String, Object>> response = socialService.validAccessToken(accessToken, socialUrl, httpMethod);
        Map<String, Object> googlePK = response.getBody();
        String id = String.valueOf(googlePK.get("id"));
        return AuthDto.OAuthIdAndRefreshTokenResponse.to(id + "@KAKAO", " @");
    }

    protected boolean revokeKakao(String userId) {
        String unlinkUrl = "https://kapi.kakao.com/v1/user/unlink";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", userId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoProperties.appAdminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<AuthDto.RevokeKakaoResponse> response = restTemplate.postForEntity(unlinkUrl, httpEntity, AuthDto.RevokeKakaoResponse.class);
        int revokeStatusCode = response.getStatusCode().value();
        return revokeStatusCode == 200;
    }
}
