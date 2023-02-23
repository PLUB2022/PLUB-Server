package plub.plubserver.domain.account;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import plub.plubserver.domain.account.dto.AuthDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.Role;
import plub.plubserver.domain.account.model.SocialType;

public class AccountTemplate {

    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private static Long id = 10000L;
    public static final String EMAIL = "sectionr0@google";
    public static final String EMAIL2 = "sectionr01@google";
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
                .nickname(NICKNAME)
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

    public static Account makeAccountAdmin(){
        return makeTestAccount(EMAIL2, PASSWORD2, NICKNAME2, BIRTHDAY2, Role.ROLE_ADMIN, GENDER2, SOCIAL_TYPE, INTRODUCE2);

    }
}
