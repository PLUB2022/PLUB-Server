package plub.plubserver.domain.account;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import plub.plubserver.domain.account.dto.AccountDto;
import plub.plubserver.domain.account.dto.AuthDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.Role;
import plub.plubserver.domain.account.model.SocialType;

public class AccountTemplate {

    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private static Long id = 10000L;
    public static final String EMAIL = "sectionr0@example.com";
    public static final String EMAIL2 = "sectionr01@example.com";
    public static final String PASSWORD = "password";
    public static final String PASSWORD2 = "password";
    public static final String NICKNAME = "nickname123";
    public static final String NICKNAME2 = "nickname1234";
    public static final String BIRTHDAY = "10/12";
    public static final String BIRTHDAY2 = "10/12";
    public static final String GENDER = "M";
    public static final String GENDER2 = "M";
    public static final String INTRODUCE = "introduce";
    public static final String INTRODUCE2 = "introduce2";
    public static final Role ROLE = Role.ROLE_USER;
    public static final SocialType SOCIAL_TYPE = SocialType.GOOGLE;

    private static Account getMakeAccount(String email, String password, String nickname, String birthday, Role role, String gender, SocialType socialType, String introduce) {
        return Account.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .nickname(nickname)
                .birthday(birthday)
                .role(role)
                .gender(gender)
                .socialType(socialType)
                .introduce(introduce)
                .build();
    }

    public static AuthDto.SignUpRequest makeSignUpRequest() {
        return AuthDto.SignUpRequest.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .socialType(SOCIAL_TYPE.toString())
                .birthday(BIRTHDAY)
                .gender(GENDER)
                .introduce(INTRODUCE)
                .build();
    }

    public static Account makeTestAccount(String email, String password, String nickname, String birthday, Role role, String gender, SocialType socialType, String introduce){
        Account account = getMakeAccount(email, password, nickname, birthday, role, gender, socialType, introduce);
        account.setIdForTest(id++);
        return account;
    }

    public static Account makeAccount1() {
        return makeTestAccount(EMAIL, PASSWORD, NICKNAME, BIRTHDAY, ROLE, GENDER, SOCIAL_TYPE, INTRODUCE);
    }

    public static Account makeAccount2() {
        return makeTestAccount(EMAIL2, PASSWORD2, NICKNAME, BIRTHDAY2, ROLE, GENDER2, SOCIAL_TYPE, INTRODUCE2);
    }

    public static AccountDto.AccountRequest makeLoginRequest1() {
        return new AccountDto.AccountRequest(EMAIL, PASSWORD, NICKNAME, SOCIAL_TYPE.getSocialName());
    }

    public static AccountDto.AccountRequest makeLoginRequest2() {
        return new AccountDto.AccountRequest(EMAIL2, PASSWORD2, NICKNAME2, SOCIAL_TYPE.getSocialName());
    }
}
