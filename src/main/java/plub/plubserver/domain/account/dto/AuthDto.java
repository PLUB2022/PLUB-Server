package plub.plubserver.domain.account.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import plub.plubserver.config.jwt.JwtDto;
import plub.plubserver.domain.account.config.AccountCode;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.Role;
import plub.plubserver.domain.account.model.SocialType;

import java.util.List;

@Slf4j
public class AuthDto {

    public record SocialLoginRequest(
            @ApiModelProperty(value = "소셜타입", example = "GOOGLE/KAKAO/APPLE")
            String socialType,
            @ApiModelProperty(value = "소셜 액세스 토큰", example = "ya29.a0Aa4xrXNXkiDBMm7MtSneVejzvupPun8S8EHorgvrt-nlCNy83PA9TI")
            String accessToken,
            @ApiModelProperty(value = "소셜 인증 토큰", example = "eyJraWQiOiJmaDZCczhDIiwiYWxnIjoiUlMyNTYif")
            String authorizationCode
    ) {
    }

    public record LoginRequest(String email, String password) {
        @Builder
        public LoginRequest {
        }

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(email, password);
        }
    }

    public record SignUpRequest(
            @ApiModelProperty(value = "이용약관 및 개인정보취급방침 (필수)", example = "true/false")
            boolean usePolicy,
            @ApiModelProperty(value = "위치 기반 서비스 이용 약관 (필수)", example = "true/false")
            boolean placePolicy,
            @ApiModelProperty(value = "만 14세 이상 확인 (필수)", example = "true/false")
            boolean agePolicy,
            @ApiModelProperty(value = "개인정보 수집 이용 동의 (필수)", example = "true/false")
            boolean personalPolicy,
            @ApiModelProperty(value = "마케팅 활용 동의 (선택)", example = "true/false")
            boolean marketPolicy,
            @ApiModelProperty(value = "닉네임", example = "플럽")
            String nickname,
            @ApiModelProperty(value = "생년월일", example = "1997-10-12")
            String birthday,
            @ApiModelProperty(value = "성별정보", example = "M/F")
            String gender,
            @ApiModelProperty(value = "자기소개", example = "안녕하세요! 저는 플럽이에요")
            String introduce,
            @ApiModelProperty(value = "관심사 선택", example = "[음악, 맛집, 뷰티, 계절스포츠]")
            List<String> categoryList

    ) {
        @Builder
        public SignUpRequest {
        }

        public SocialType getSocialType(String socialType) {
            if (socialType.equalsIgnoreCase(SocialType.GOOGLE.name())) {
                return SocialType.GOOGLE;
            } else if (socialType.equalsIgnoreCase(SocialType.KAKAO.name())) {
                return SocialType.KAKAO;
            } else if (socialType.equalsIgnoreCase(SocialType.APPLE.name())) {
                return SocialType.APPLE;
            } else throw new AccountException(AccountCode.SOCIAL_TYPE_ERROR);
        }

        public Account toAccount(String email, String socialType, PasswordEncoder passwordEncoder) {
            return Account.builder()
                    .email(email)
                    .password(passwordEncoder.encode(email + "plub"))
                    .socialType(getSocialType(socialType))
                    .nickname(nickname)
                    .birthday(birthday)
                    .gender(gender)
                    .introduce(introduce)
                    .role(Role.ROLE_USER)
                    .build();
        }

        public Account toAdmin(PasswordEncoder passwordEncoder, CharSequence ADMIN_PASSWORD) {
            return Account.builder()
                    .email("admin")
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .socialType(SocialType.GOOGLE)
                    .nickname("admin")
                    .birthday("birthday admin")
                    .gender("M")
                    .introduce("introduce admin")
                    .role(Role.ROLE_ADMIN)
                    .build();
        }
    }

    public record ReissueRequest(
            @ApiModelProperty(value = "갱신토큰값", example = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NjQ2")
            String refreshToken
    ) {
    }

    public record RevokeKakaoResponse(String id) {

    }

    public record AuthMessage(int statusCode, Object detailData, String detailMessage) {
    }

    public record SignAuthMessage(int statusCode, JwtDto detailData, String detailMessage) {
    }

    public record SigningAccount(String email, String socialType, String refreshToken) {
    }

    public record SignToken(String signToken) {
    }

    public record OAuthIdAndRefreshTokenResponse(
            String id,
            String refreshToken
    ) {
        public static OAuthIdAndRefreshTokenResponse to(String userId, String refreshToken) {
            return new OAuthIdAndRefreshTokenResponse(userId, refreshToken);
        }
    }
}
