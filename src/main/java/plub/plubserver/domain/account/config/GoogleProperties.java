package plub.plubserver.domain.account.config;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@NoArgsConstructor
public class GoogleProperties {

    @Value("${google.client-id}")
    public String CLIENT_ID;
    @Value("${google.client_secret}")
    public String CLIENT_SECRET;
    @Value("${google.redirect_uri}")
    public String REDIRECT_URI;
}