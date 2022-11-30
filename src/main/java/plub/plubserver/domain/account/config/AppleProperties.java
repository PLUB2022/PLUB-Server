package plub.plubserver.domain.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppleProperties {

    @Value("${apple.appleBundleId}")
    public String appleBundleId;
    @Value("${apple.appleTeamId}")
    public String appleTeamId;
    @Value("${apple.appleSignKeyId}")
    public Object appleSignKeyId;
    @Value("${apple.appleSignKeyFilePath}")
    public String appleSignKeyFilePath;
}
