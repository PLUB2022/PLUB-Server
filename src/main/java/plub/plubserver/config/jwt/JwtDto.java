package plub.plubserver.config.jwt;

public record JwtDto(
        String accessToken,
        String refreshToken
) {}
