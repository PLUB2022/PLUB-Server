package plub.plubserver.domain.report.repositoy;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.model.ReportTarget;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Long countByTargetIdAndReportTargetAndCheckCanceledFalse(Long targetId, ReportTarget reportTarget);

    List<Report> findAllByTargetIdAndReportTargetAndCheckCanceledFalse(Long targetId, ReportTarget reportTarget);

    boolean existsByReporterAndReportedAccountAndReportTargetAndCheckCanceledFalse(
            Account reporter,
            Account reportedAccount,
            ReportTarget reportTarget
    );

    Long countByReporterAndCreatedAtBetween(Account reporter, String start, String end);

    List<Report> findAllByReporter(Account reporter);
}
