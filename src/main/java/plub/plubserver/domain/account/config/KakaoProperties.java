package plub.plubserver.domain.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KakaoProperties {
    @Value("${kakao.appAdminKey}")
    public String appAdminKey;
}
