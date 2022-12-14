package plub.plubserver.domain.plubbing.model;

import lombok.*;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.model.PlubbingFeed;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountPlubbing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_plubbing_id")
    private Long id;

    private boolean isHost;
    @Enumerated(EnumType.STRING)
    private AccountPlubbingStatus accountPlubbingStatus;

    // 회원_모임페이지(다) - 회원(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 회원_모임페이지(다) - 모임(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubbing_id")
    private Plubbing plubbing;

    // 회원_모임페이지(1) - 아카이브(다)
    @OneToMany(mappedBy = "accountPlubbing", cascade = CascadeType.ALL)
    private List<PlubbingFeed> archives = new ArrayList<>();

    @Builder
    public AccountPlubbing(boolean isHost, AccountPlubbingStatus accountPlubbingStatus, Account account, Plubbing plubbing, List<PlubbingFeed> archives) {
        this.isHost = isHost;
        this.accountPlubbingStatus = accountPlubbingStatus;
        this.account = account;
        this.plubbing = plubbing;
        this.archives = archives;
    }

    public void changeStatus(AccountPlubbingStatus accountPlubbingStatus) {
        this.accountPlubbingStatus = accountPlubbingStatus;
    }
}