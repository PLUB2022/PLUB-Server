package plub.plubserver.domain.report.repositoy;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.report.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Long countByReportedAccount(Account account);
}
