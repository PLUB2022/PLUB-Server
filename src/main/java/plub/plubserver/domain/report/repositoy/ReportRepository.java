package plub.plubserver.domain.report.repositoy;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.model.ReportTarget;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Long countByReportedAccountAndCheckCanceledFalse(Account account);

    boolean existsByReporterAndReportedAccountAndReportTargetAndCheckCanceledFalse(
            Account reporter,
            Account reportedAccount,
            ReportTarget reportTarget
    );
}
