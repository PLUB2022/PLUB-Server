package plub.plubserver.domain.account.dto;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.Role;
import plub.plubserver.domain.account.model.SocialType;

@Slf4j
public class AuthDto {

    public record SocialLoginRequest(String accessToken, String socialType) {}

    @Builder
    public record LoginRequest(String email, String password) {
        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(email,password);
        }
    }

    public record SignUpRequest(
            String email,
            String socialType,
            String nickname,
            String birthday,
            String gender,
            String introduce
    ) {
        public SocialType getSocialType() {
            if (socialType.equalsIgnoreCase(SocialType.GOOGLE.name())) {
                return SocialType.GOOGLE;
            } else {
                return SocialType.KAKAO;
            }
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

    public record ReissueRequest(String refreshToken) {}

    public record AuthMessage(Object detailData, String detailMessage) {
    }

    public record SigningAccount(String email, String socialType) {

    }
}
