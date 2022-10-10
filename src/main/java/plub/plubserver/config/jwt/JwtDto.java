package plub.plubserver.config.jwt;

import io.swagger.annotations.ApiModelProperty;

public record JwtDto(
        @ApiModelProperty(value = "인증토큰값",example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2a2RuanJsY2pzQG5hdmV")
        String accessToken,
        @ApiModelProperty(value = "갱신토큰값",example = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NjUzODc3NjQsImV4cCI")
        String refreshToken
) {}
