package plub.plubserver.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.SocialType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String email;
    private String socialType;
    private String nickname;
    private String birthday;
    private String gender;
    private String introduce;

    public SocialType getSocialType() {
        if (socialType.equalsIgnoreCase(SocialType.GOOGLE.name())) {
            return SocialType.GOOGLE;
        } else {
            return SocialType.KAKAO;
        }
    }

    public Account toAccount() {
        return Account.builder()
                .email(email)
                .socialType(getSocialType())
                .nickname(nickname)
                .birthday(birthday)
                .gender(gender)
                .introduce(introduce)
                .build();
    }
}
