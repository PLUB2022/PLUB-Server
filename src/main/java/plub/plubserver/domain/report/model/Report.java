package plub.plubserver.domain.report.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.report.config.ReportStatusMessage;

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

    private String reportReason;

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

    private Long plubbingId;

    private ReportStatusMessage reportStatusMessage;

    public void setReportStatusMessage(ReportStatusMessage reportStatusMessage) {
        this.reportStatusMessage = reportStatusMessage;
    }

    public void cancelReport(boolean checkCanceled) {
        this.checkCanceled = checkCanceled;
        this.canceledDate = LocalDateTime.now();
    }
}
