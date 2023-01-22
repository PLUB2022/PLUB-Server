package plub.plubserver.domain.account.model;

import lombok.*;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.alarm.model.Alarm;
import plub.plubserver.domain.feed.model.PlubbingFeed;
import plub.plubserver.domain.message.model.Message;
import plub.plubserver.domain.policy.model.Policy;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.recruit.model.AppliedAccount;
import plub.plubserver.domain.recruit.model.Bookmark;
import plub.plubserver.notice.model.PlubbingNotice;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static plub.plubserver.domain.account.dto.AccountDto.AccountRequest;

@Entity
@Getter
@Builder
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

    // 회원(1) - 회원_모집페이지(다) # 다대다 용
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppliedAccount> appliedAccountList = new ArrayList<>();

    // 회원(1) - 회원_모임페이지(다) # 다대다 용
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountPlubbing> accountPlubbingList = new ArrayList<>();

    // 회원(1) - 회원_카테고리(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountCategory> accountCategories = new ArrayList<>();

    // 회원(1) - 정책(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Policy> policyList = new ArrayList<>();

    // 회원(1) - 북마크(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();

    // 회원(1) - 게시판(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingFeed> feedList = new ArrayList<>();

    // 회원(1) - 공지(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingNotice> noticeList = new ArrayList<>();

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

    public void setAccountCategory(List<AccountCategory> accountCategories) {
        this.getAccountCategories().clear();
        this.getAccountCategories().addAll(accountCategories);
    }

    public void addBookmark(Bookmark bookmark) {
        if (bookmarkList == null) bookmarkList = new ArrayList<>();
        bookmarkList.add(bookmark);
    }

    public void removeBookmark(Bookmark bookmark) {
        bookmarkList.remove(bookmark);
    }
}
