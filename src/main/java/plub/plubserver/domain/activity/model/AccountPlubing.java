package plub.plubserver.domain.activity.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.archive.model.Archive;
import plub.plubserver.domain.plubing.model.Plubing;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountPlubing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_plubing_id")
    private Long id;

    private boolean isHost;
    private PlubingStatus status;

    // 회원_모임페이지(다) - 회원(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 회원_모임페이지(다) - 모임(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubing_id")
    private Plubing plubing;

    // 회원_모임페이지(1) - 아카이브(다)
    @OneToMany(mappedBy = "accountPlubing", cascade = CascadeType.ALL)
    private List<Archive> archives = new ArrayList<>();
}