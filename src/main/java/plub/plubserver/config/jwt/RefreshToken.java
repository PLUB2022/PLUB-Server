package plub.plubserver.config.jwt;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken")
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String accountId;
    @Indexed
    private String refreshToken;

    public void replace(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
