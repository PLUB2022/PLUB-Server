package plub.plubserver.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class AccountDto {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MemberRequest {
        private String email;
        private String password;
        private String nickname;
        private String socialType;

        public AuthDto.LoginRequest toLoginRequest() {
            return AuthDto.LoginRequest.builder()
                    .email(email)
                    .password(password)
                    .build();
        }
    }
}
