package plub.plubserver.domain.account.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.alarm.model.Alarm;
import plub.plubserver.domain.comment.model.Comment;
import plub.plubserver.domain.message.model.Message;
import plub.plubserver.domain.policy.model.Policy;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.recruit.model.AccountRecruit;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static plub.plubserver.domain.account.dto.AccountDto.AccountRequest;

@Entity
@Getter
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
    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    private String profileImage;
    private String lastLogin;
    private String fcmToken;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String providerRefreshToken;

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
    private List<AccountRecruit> accountRecruitList = new ArrayList<>();

    // 회원(1) - 회원_모임페이지(다) # 다대다 용
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountPlubbing> accountPlubbingList = new ArrayList<>();

    // 회원(1) - 회원_카테고리(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountCategory> accountCategories = new ArrayList<>();

    // 회원(1) - 정책(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Policy> policyList = new ArrayList<>();

    @Builder
    public Account(String email, String password, String nickname, int age, String birthday, String gender, String phone, SocialType socialType, String profileImage, String lastLogin, String fcmToken, Role role, String introduce, List<AccountCategory> accountCategories) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.age = age;
        this.birthday = birthday;
        this.gender = gender;
        this.phone = phone;
        this.socialType = socialType;
        this.profileImage = profileImage;
        this.lastLogin = lastLogin;
        this.fcmToken = fcmToken;
        this.introduce = introduce;
        this.role = role;
        this.accountCategories = accountCategories;
    }

    // TODO : DTO에 변환로직이 가도록 수정해야함
    public AccountRequest toAccountRequestDto() {
        return new AccountRequest(email, email + "plub", nickname, socialType.getSocialName());
    }

    public void setIdForTest(Long id) {
        this.id = id;
    }

    public void updateProfile(String newNickname, String newIntroduce, String newProfileImage) {
        this.nickname = newNickname;
        this.profileImage = newProfileImage;
        this.introduce = newIntroduce;
    }

    public void updateRefreshToken(String refreshToken) {
        this.providerRefreshToken = refreshToken;
    }

    public void updateAccountCategory(List<AccountCategory> accountCategories) {
        this.accountCategories = accountCategories;
    }

    public void updateAccountPolicy(List<Policy> policyList) {
        this.policyList = policyList;
    }
}
