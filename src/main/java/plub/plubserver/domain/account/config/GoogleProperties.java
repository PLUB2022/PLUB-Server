package plub.plubserver.domain.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleProperties {

    @Value("${google.client-id}")
    public String clientId;
    @Value("${google.client_secret}")
    public String clientSecret;
    @Value("${google.redirect_uri}")
    public String redirectUri;
}