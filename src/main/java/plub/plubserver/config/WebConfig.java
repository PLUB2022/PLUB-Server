package plub.plubserver.config;

import lombok.RequiredArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import plub.plubserver.util.IpTrackInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public JSONParser jsonParser() {
        return new JSONParser();
    }

    private final IpTrackInterceptor ipTrackInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ipTrackInterceptor);
    }
}
