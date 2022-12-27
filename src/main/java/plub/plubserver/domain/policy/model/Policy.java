package plub.plubserver.domain.policy.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import plub.plubserver.domain.account.model.Account;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Long id;

    private String title;
    private boolean isChecked;

    // 회원(1) - 정책(다)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public static Policy toPolicy(String title){
        return Policy.builder()
                .title(title)
                .isChecked(false)
                .build();
    }

}
