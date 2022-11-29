package plub.plubserver.domain.account.dto;

public class GoogleDto {
    public record GoogleTokenResponse(
            String access_token,
            int expires_in,
            String scope,
            String refresh_token,
            String token_type,
            String id_token
    ) {}

    public record GoogleRefreshTokenResponse(
            String access_token,
            int expires_in,
            String scope,
            String token_type
    ) {}
}
