package plub.plubserver.domain.report.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    private String content;

    @Enumerated(EnumType.STRING)
    private ReportTarget reportTarget;

    private Long targetId;

    private boolean checkCanceled;

    private LocalDateTime canceledDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_account_id")
    private Account reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_account_id")
    private Account reportedAccount;


}
