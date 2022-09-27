package plub.plubserver.domain.account.dto;

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


    public record AppleAuthTokenResponse(
            String accessToken,
            String expiresIn,
            String idToken,
            String refreshToken,
            String tokenType
    ){
    }
}
