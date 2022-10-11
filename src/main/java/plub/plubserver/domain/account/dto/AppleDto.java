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

    public record AppleCodeResponse(
            String access_token,
            int expires_in,
            String id_token,
            String refresh_token,
            String token_type
    ){}
}
