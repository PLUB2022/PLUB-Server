package plub.plubserver.domain.account.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.activity.model.AccountPlubing;
import plub.plubserver.domain.alarm.model.Alarm;
import plub.plubserver.domain.comment.model.Comment;
import plub.plubserver.domain.message.model.Message;
import plub.plubserver.domain.recruit.model.AccountBoard;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    private String email;
    private String name;
    private int age;
    private String birthday;
    private String gender;
    private String phone;
    private String provider;
    private String profile; // saved_path
    private String lastLogin;
    private String fcmToken;

    // 회원(1) - 차단 사용자(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<BanAccount> bannedAccounts = new ArrayList<>();

    // 회원(1) - 알람(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Alarm> alarms = new ArrayList<>();

    // 회원(1) - 쪽지(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    // 회원(1) - 댓글(다)
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    // 회원(1) - 회원_모집페이지(다) # 다대다 용
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<AccountBoard> accountBoardList = new ArrayList<>();

    // 회원(1) - 회원_모임페이지(다) # 다대다 용
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<AccountPlubing> accountPlubingList = new ArrayList<>();
}
