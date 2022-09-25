package plub.plubserver.domain.account.dto;

import static plub.plubserver.domain.account.dto.AuthDto.LoginRequest;

public class AccountDto {
    public record AccountRequest(
            String email,
            String password,
            String nickname,
            String socialType
    ) {
        public LoginRequest toLoginRequest() {
            return LoginRequest.builder()
                    .email(email)
                    .password(password)
                    .build();
        }
    }

    public record AccountResponse(Object data,  String msg) {
    }
}
