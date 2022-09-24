package plub.plubserver.domain.account.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

public class AppleDto {

    @Getter
    @Setter
    public class ApplePublicKeyResponse {
        private List<AppleKey> keys;
        @Getter
        @Setter
        public static class AppleKey {
            private String kty;
            private String kid;
            private String use;
            private String alg;
            private String n;
            private String e;

        }

        public Optional<AppleKey> getMatchedKeyBy(String kid, String alg){
            return this.keys.stream()
                    .filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
                    .findFirst();
        }
    }

}
