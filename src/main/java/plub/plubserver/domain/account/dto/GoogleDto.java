package plub.plubserver.domain.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleDto {
    public record GoogleTokenResponse(
            @JsonProperty("access_token")
            String accessToken,
            @JsonProperty("expires_in")
            int expiresIn,
            @JsonProperty("scope")
            String scope,
            @JsonProperty("refresh_token")
            String refreshToken,
            @JsonProperty("token_type")
            String tokenType,
            @JsonProperty("id_token")
            String idToken
    ) {
    }

    public record GoogleRefreshTokenResponse(
            @JsonProperty("access_token")
            String accessToken,
            @JsonProperty("expires_in")
            int expiresIn,
            @JsonProperty("scope")
            String scope,
            @JsonProperty("token_type")
            String tokenType
    ) {
    }
}
