package plub.plubserver.domain.plubbing.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountPlubbing extends BaseEntity {

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

    /**
     * methods
     */
    public void changeStatus(AccountPlubbingStatus accountPlubbingStatus) {
        this.accountPlubbingStatus = accountPlubbingStatus;
    }

    public void changeHost() {
        this.isHost = !this.isHost;
    }

    public void updateAccountPlubbingStatus(AccountPlubbingStatus accountPlubbingStatus) {
        this.accountPlubbingStatus = accountPlubbingStatus;
    }
}