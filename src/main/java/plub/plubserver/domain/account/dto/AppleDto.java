package plub.plubserver.domain.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class AppleDto {
    protected String id;
    private String token;
    private List<AppleKey> keys;

    public record AppleKey(
            String kty,
            String kid,
            String use,
            String alg,
            String n,
            String e
    ) {
    }

    public Optional<AppleKey> getMatchedKeyBy(String kid, String alg) {
        return keys.stream()
                .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                .findFirst();
    }

    public record AppleCodeResponse(
            @JsonProperty("access_token")
            String accessToken,
            @JsonProperty("expires_in")
            int expiresIn,
            @JsonProperty("id_token")
            String idToken,
            @JsonProperty("refresh_token")
            String refreshToken,
            @JsonProperty("token_type")
            String tokenType
    ) {
    }

    public record AppleRefreshTokenResponse(
            @JsonProperty("access_token")
            String accessToken,
            @JsonProperty("expires_in")
            int expiresIn,
            @JsonProperty("token_type")
            String tokenType,
            @JsonProperty("id_token")
            String idToken
    ) {
    }
}
