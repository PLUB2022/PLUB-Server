package plub.plubserver.domain.account.model;


import lombok.*;
import plub.plubserver.common.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SuspendAccount extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "suspend_account_id")
    private Long id;
    private LocalDateTime startedSuspendedDate;
    private LocalDateTime endedSuspendedDate;
    private Long accountId;
    private String accountEmail;
    private String accountDI;

    private boolean isSuspended;

    public void setSuspendedDate() {
        this.startedSuspendedDate = LocalDateTime.now();
        this.endedSuspendedDate = LocalDateTime.now().plusMonths(6);
    }

    public void setSuspended(boolean suspended) {
        isSuspended = suspended;
    }
}
