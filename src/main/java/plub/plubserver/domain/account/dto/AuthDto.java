package plub.plubserver.domain.account.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import plub.plubserver.config.jwt.JwtDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.Role;
import plub.plubserver.domain.account.model.SocialType;
import plub.plubserver.exception.account.InvalidSocialTypeException;

@Slf4j
public class AuthDto {

    public record SocialLoginRequest(
            @ApiModelProperty(value = "소셜타입", example = "GOOGLE/KAKAO/APPLE")
            String socialType,
            @ApiModelProperty(value = "소셜 액세스 토큰", example = "ya29.a0Aa4xrXNXkiDBMm7MtSneVejzvupPun8S8EHorgvrt-nlCNy83PA9TI")
            String accessToken,
            @ApiModelProperty(value = "소셜 인증 토큰", example = "eyJraWQiOiJmaDZCczhDIiwiYWxnIjoiUlMyNTYif")
            String authorizationCode
    ) {}

    public record LoginRequest(String email, String password) {
        @Builder public LoginRequest{}
        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(email,password);
        }
    }

    public record SignUpRequest(
            @ApiModelProperty(value = "이메일",example = "plub@example.com")
            String email,
            @ApiModelProperty(value = "닉네임", example = "플럽")
            String nickname,
            @ApiModelProperty(value = "소셜타입", example = "GOOGLE/KAKAO/APPLE")
            String socialType,
            @ApiModelProperty(value = "생년월일", example = "19971012")
            String birthday,
            @ApiModelProperty(value = "성별정보", example = "M/F")
            String gender,
            @ApiModelProperty(value = "자기소개", example = "안녕하세요! 저는 플럽이에요")
            String introduce
    ) {
        @Builder public SignUpRequest{}
        public SocialType getSocialType() {
            if (socialType.equalsIgnoreCase(SocialType.GOOGLE.name())) {
                return SocialType.GOOGLE;
            } else if (socialType.equalsIgnoreCase(SocialType.KAKAO.name())) {
                return SocialType.KAKAO;
            } else if (socialType.equalsIgnoreCase(SocialType.APPLE.name())) {
                return SocialType.APPLE;
            } else throw new InvalidSocialTypeException();
        }
        public Account toAccount(PasswordEncoder passwordEncoder) {
            return Account.builder()
                    .email(email)
                    .password(passwordEncoder.encode(email+"plub"))
                    .socialType(getSocialType())
                    .nickname(nickname)
                    .birthday(birthday)
                    .gender(gender)
                    .introduce(introduce)
                    .role(Role.ROLE_USER)
                    .build();
        }
    }

    public record ReissueRequest(
            @ApiModelProperty(value = "갱신토큰값",example = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NjQ2")
            String refreshToken
    ) {}

    public record RevokeRequest(
            @ApiModelProperty(value = "이메일",example = "plub@example.com")
            String email,
            @ApiModelProperty(value = "소셜타입", example = "GOOGLE/KAKAO/APPLE")
            String socialType,
            @ApiModelProperty(value = "소셜 액세스 토큰", example = "ya29.a0Aa4xrXNXkiDBMm7MtSneVejzvupPun8S8EHorgvrt-nlCNy83PA9TI")
            String accessToken,
            @ApiModelProperty(value = "소셜 인증 토큰", example = "eyJraWQiOiJmaDZCczhDIiwiYWxnIjoiUlMyNTYif")
            String authorizationCode,
            @ApiModelProperty(value = "카카오 전용 값", example = "1293127391230")
            String userId
    ) {

    }
    public record RevokeKakaoResponse(String id) {

    }
    public record AuthMessage(Object detailData, String detailMessage) {
    }

    public record SignAuthMessage(JwtDto detailData, String detailMessage) {
    }

    public record SigningAccount(String email, String socialType) {
    }
}
