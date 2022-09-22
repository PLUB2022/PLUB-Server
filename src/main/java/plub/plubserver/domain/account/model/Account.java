package plub.plubserver.domain.account.model;

import lombok.*;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.config.jwt.RefreshToken;
import plub.plubserver.domain.account.dto.AccountDto;
import plub.plubserver.domain.activity.model.AccountPlubing;
import plub.plubserver.domain.alarm.model.Alarm;
import plub.plubserver.domain.category.model.Category;
import plub.plubserver.domain.comment.model.Comment;
import plub.plubserver.domain.message.model.Message;
import plub.plubserver.domain.recruit.model.AccountBoard;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    private String email;
    private String password;
    private String nickname;
    private int age;
    private String birthday;
    private String gender;
    private String phone;
    private String introduce;
    private SocialType socialType;
    private String profile; // saved_path
    private String lastLogin;
    private String fcmToken;
    private Role role;

    // 회원(1) - 차단 사용자(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BanAccount> bannedAccounts = new ArrayList<>();

    // 회원(1) - 알람(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alarm> alarms = new ArrayList<>();

    // 회원(1) - 쪽지(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    // 회원(1) - 댓글(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 회원(1) - 회원_모집페이지(다) # 다대다 용
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountBoard> accountBoardList = new ArrayList<>();

    // 회원(1) - 회원_모임페이지(다) # 다대다 용
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountPlubing> accountPlubingList = new ArrayList<>();

    // 회원(1) - 카테고리(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;

    @Builder
    public Account(String email, String password, String nickname, int age, String birthday, String gender, String phone, SocialType socialType, String profile, String lastLogin, String fcmToken, Role role, String introduce) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.age = age;
        this.birthday = birthday;
        this.gender = gender;
        this.phone = phone;
        this.socialType = socialType;
        this.profile = profile;
        this.lastLogin = lastLogin;
        this.fcmToken = fcmToken;
        this.introduce = introduce;
        this.role = role;
    }


    public AccountDto.MemberRequest toAccountRequestDto(){
        return new AccountDto.MemberRequest(email,email+"plub",nickname, socialType.getSocialName());
    }
}
