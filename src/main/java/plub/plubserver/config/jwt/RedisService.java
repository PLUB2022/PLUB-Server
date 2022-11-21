package plub.plubserver.config.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean deleteRefreshToken(Long accountId) {
        if (redisTemplate.hasKey(String.valueOf(accountId))) {
            redisTemplate.delete(String.valueOf(accountId));
            return true;
        } else {
            return false;
        }
    }

    public boolean setRefreshToken(Long accountId, String refresh) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(String.valueOf(accountId), refresh);
        return true;
    }

    public String getRefreshToken(Long accountId) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(String.valueOf(accountId))) {
            return operations.get(String.valueOf(accountId));
        } else {
            return null;
        }
    }
}