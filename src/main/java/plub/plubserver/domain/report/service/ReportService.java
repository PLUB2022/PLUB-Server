package plub.plubserver.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.report.config.ReportMessage;
import plub.plubserver.domain.report.dto.ReportDto.*;
import plub.plubserver.domain.report.dto.ReportDto.ReportResponse;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.model.ReportTarget;
import plub.plubserver.domain.report.model.ReportType;
import plub.plubserver.domain.report.repositoy.ReportRepository;

import java.util.List;

import static plub.plubserver.domain.report.config.ReportConstant.REPORT_PLUBBING_PAUSE_COUNT;
import static plub.plubserver.domain.report.config.ReportConstant.REPORT_WARNING_PUSH_COUNT;
import static plub.plubserver.domain.report.dto.ReportDto.CreateReportRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final AccountService accountService;
    private final NotificationService notificationService;

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

    public ReportResponse notifyHost(Report report, Plubbing plubbing) {
        Long reportCount = getReportCount(report.getTargetId(), report.getReportTarget());
        if (reportCount >= REPORT_PLUBBING_PAUSE_COUNT) {
            // 모임 정지
            notificationService.pushMessage(
                    plubbing.getHost(),
                    "신고",
                    plubbing.getName() + "모임장 경고 3회 누적으로 모임이 영구정지 되었습니다."
            );
            return ReportResponse.of(report, ReportMessage.REPORT_PLUBBING_PAUSED);
        }
        if (reportCount >= REPORT_WARNING_PUSH_COUNT) {
            // 모임장에게 경고 알림
            notificationService.pushMessage(
                    plubbing.getHost(),
                    "신고",
                    plubbing.getName() + "에 6회 이상 다른 사용자의 신고가 누적되어 되었습니다."
            );
            return ReportResponse.of(report, ReportMessage.REPORT_HOST_NOTIFY);
        }
        return ReportResponse.of(report, ReportMessage.REPORT_SUCCESS);
    }

    public List<ReportTypeResponse> getReportType() {
        return List.of(
                new ReportTypeResponse(ReportType.BAD_WORDS.toString(), ReportType.BAD_WORDS.getDetailContent()),
                new ReportTypeResponse(ReportType.FALSE_FACT.toString(), ReportType.FALSE_FACT.getDetailContent()),
                new ReportTypeResponse(ReportType.ADVERTISEMENT.toString(), ReportType.ADVERTISEMENT.getDetailContent()),
                new ReportTypeResponse(ReportType.ETC.toString(), ReportType.ETC.getDetailContent()));
    }
}
