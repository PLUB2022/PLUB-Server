package plub.plubserver.domain.account.dto;

import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class AppleDto {

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
}
