package plub.plubserver.domain.account.model;

import lombok.Getter;
import org.springframework.http.HttpMethod;

@Getter
public enum SocialType {

    KAKAO(
            "kakao",
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.GET
    ),

    GOOGLE(
            "google",
            "https://www.googleapis.com/oauth2/v3/userinfo",
            HttpMethod.GET
    );

    private String socialName;
    private String socialUrl;
    private HttpMethod httpMethod;

    SocialType(String socialName, String socialUrl, HttpMethod httpMethod) {
        this.socialName = socialName;
        this.socialUrl = socialUrl;
        this.httpMethod = httpMethod;
    }
}
