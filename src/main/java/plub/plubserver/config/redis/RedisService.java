package plub.plubserver.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final int LIMIT_TIME = 5 * 60;

    private final StringRedisTemplate template;

    public String getSmsCertification(String phone) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        return valueOperations.get(phone);
    }

    public boolean hasKey(String phone) {
        return Boolean.TRUE.equals(template.hasKey(phone));
    }

    public void createSmsCertification(String phone, String certificationNum) {
        ValueOperations<String, String> valueOperations = template.opsForValue();
        Duration expireDuration = Duration.ofSeconds(LIMIT_TIME);
        valueOperations.set(phone, certificationNum, expireDuration);
    }

    public void deleteSmsCertification(String phone) {
        template.delete(phone);
    }
}
