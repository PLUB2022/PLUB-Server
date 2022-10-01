package plub.plubserver.domain.account.dto;

import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.SocialType;

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

    public record AccountInfo(
            String email,
            String nickname,
            SocialType socialType,
            String birthday,
            String gender,
            String introduce
    ){

        public static AccountInfo of(Account account) {
            return new AccountInfo(account.getEmail(), account.getNickname(),account.getSocialType(), account.getBirthday(), account.getGender(), account.getIntroduce());
        }
    }

    public record AccountNicknameRequest(String nickname) {
    }

    public record AccountIntroduceRequest(String introduce) {
    }

}
