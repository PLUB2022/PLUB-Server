package plub.plubserver.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.model.ReportTarget;
import plub.plubserver.domain.report.repositoy.ReportRepository;

import static plub.plubserver.domain.report.dto.ReportDto.CreateReportRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final AccountService accountService;

    // 신고하기
    @Transactional
    public Report createReport(CreateReportRequest request, Account loginAccount) {
        Account reporter = accountService.getAccount(loginAccount.getId());
        Report report = reportRepository.save(request.toEntity(reporter));
        reporter.addReport(report);
        return report;
    }

    public Long getReportCount(Long targetId, ReportTarget target) {
        return reportRepository.countByReportTargetIdAndReportTarget(targetId, target);
    }
}
