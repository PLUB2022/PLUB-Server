package plub.plubserver.domain.account.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AccountNicknameHistory {
    @Id
    @GeneratedValue
    private Long id;
    private String nickname;
    private LocalDateTime changedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
}
