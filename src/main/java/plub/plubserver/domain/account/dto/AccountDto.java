package plub.plubserver.domain.account.dto;

public class AccountDto {
    public record MemberRequest(
            String email,
            String password,
            String nickname,
            String socialType
    ) {
        public AuthDto.LoginRequest toLoginRequest() {
            return AuthDto.LoginRequest.builder()
                    .email(email)
                    .password(password)
                    .build();
        }
    }
}
