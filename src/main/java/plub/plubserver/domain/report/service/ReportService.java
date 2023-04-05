package plub.plubserver.domain.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.SuspendAccount;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.domain.account.repository.SuspendAccountRepository;
import plub.plubserver.domain.archive.exception.ArchiveException;
import plub.plubserver.domain.archive.repository.ArchiveRepository;
import plub.plubserver.domain.feed.exception.FeedException;
import plub.plubserver.domain.feed.repository.FeedCommentRepository;
import plub.plubserver.domain.feed.repository.FeedRepository;
import plub.plubserver.domain.notice.exception.NoticeException;
import plub.plubserver.domain.notice.repository.NoticeCommentRepository;
import plub.plubserver.domain.notification.dto.NotificationDto;
import plub.plubserver.domain.notification.dto.NotificationDto.NotifyParams;
import plub.plubserver.domain.notification.model.NotificationType;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.recruit.exception.RecruitException;
import plub.plubserver.domain.recruit.repository.RecruitRepository;
import plub.plubserver.domain.report.config.ReportMessage;
import plub.plubserver.domain.report.dto.ReportDto.ReportResponse;
import plub.plubserver.domain.report.dto.ReportDto.ReportTypeResponse;
import plub.plubserver.domain.report.exception.ReportException;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.model.ReportTarget;
import plub.plubserver.domain.report.model.ReportType;
import plub.plubserver.domain.report.repositoy.ReportRepository;
import plub.plubserver.domain.todo.exception.TodoException;
import plub.plubserver.domain.todo.repository.TodoRepository;

import java.util.List;

import static plub.plubserver.domain.account.model.AccountStatus.PAUSED;
import static plub.plubserver.domain.account.model.AccountStatus.PERMANENTLY_BANNED;
import static plub.plubserver.domain.notification.model.NotificationType.*;
import static plub.plubserver.domain.report.config.ReportConstant.*;
import static plub.plubserver.domain.report.config.ReportMessage.*;
import static plub.plubserver.domain.report.dto.ReportDto.CreateReportRequest;
import static plub.plubserver.domain.report.dto.ReportDto.ReportIdResponse;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    private final SuspendAccountRepository suspendAccountRepository;

    private final FeedRepository feedRepository;
    private final TodoRepository todoRepository;
    private final FeedCommentRepository feedCommentRepository;
    private final NoticeCommentRepository noticeCommentRepository;
    private final ArchiveRepository archiveRepository;
    private final RecruitRepository recruitRepository;


    // 신고하기
    @Transactional
    public ReportIdResponse createReport(CreateReportRequest request, Account reporter) {
        Account reportedAccount = checkReportTargetAccount(request.reportTargetId(), request.reportTarget());
        Report createReport = request.toEntity(reporter, reportedAccount);
        checkDuplicateReport(createReport);
        Report report = reportRepository.save(createReport);
        return ReportIdResponse.of(report);
    }

    // 한 유저가 같은 대상을 중복 신고하는 것을 방지
    private void checkDuplicateReport(Report createReport) {
        if (reportRepository.existsByReporterAndReportedAccountAndReportTargetAndCheckCanceledFalse(
                createReport.getReporter(), createReport.getReportedAccount(), createReport.getReportTarget())) {
            throw new ReportException(StatusCode.DUPLICATE_REPORT);
        }
    }

    public Account checkReportTargetAccount(Long targetId, String reportTarget) {
        ReportTarget target = ReportTarget.toEnum(reportTarget);
        Account account = switch (target) {
            case ACCOUNT ->
                    accountRepository.findById(targetId).orElseThrow(() -> new AccountException(StatusCode.NOT_FOUND_ACCOUNT));
            case FEED ->
                    feedRepository.findById(targetId).orElseThrow(() -> new FeedException(StatusCode.NOT_FOUND_FEED)).getAccount();
            case FEED_COMMENT ->
                    feedCommentRepository.findById(targetId).orElseThrow(() -> new FeedException(StatusCode.NOT_FOUND_COMMENT)).getAccount();
            case NOTICE_COMMENT ->
                    noticeCommentRepository.findById(targetId).orElseThrow(() -> new NoticeException(StatusCode.NOT_FOUND_COMMENT)).getAccount();
            case TODO ->
                    todoRepository.findById(targetId).orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO)).getAccount();
            case ARCHIVE ->
                    archiveRepository.findById(targetId).orElseThrow(() -> new ArchiveException(StatusCode.NOT_FOUND_ARCHIVE)).getAccount();
            case RECRUIT ->
                    recruitRepository.findById(targetId).orElseThrow(() -> new RecruitException(StatusCode.NOT_FOUND_RECRUIT)).getPlubbing().getHost();
            default -> throw new ReportException(StatusCode.REPORT_TARGET_NOT_FOUND);
        };
        return account;
    }


    public Long countReportedAccount(Account reportedAccount) {
        return reportRepository.countByReportedAccountAndCheckCanceledFalse(reportedAccount);
    }

    public ReportResponse notifyReportedAccount(Report report) {
        Account reportedAccount = report.getReportedAccount();
        Long reportedAccountCount = countReportedAccount(reportedAccount);
        if (reportedAccountCount >= REPORT_ACCOUNT_BAN_COUNT) {
            // 계정 영구 정지
            NotifyParams params = createNotifyParams(report, REPORT_TITLE, REPORT_ACCOUNT_BAN_CONTENT, BAN_PERMANENTLY);
            notificationService.pushMessage(params);
            reportedAccount.updateAccountStatus(PERMANENTLY_BANNED);

            // 디비에 영구 계정 등록
            SuspendAccount suspendAccount = SuspendAccount.builder()
                    .account(reportedAccount)
                    .isSuspended(true)
                    .build();
            suspendAccount.setSuspendedDate();
            suspendAccountRepository.save(suspendAccount);

            return ReportResponse.of(report, ReportMessage.REPORT_ACCOUNT_BANNED);
        } else if (reportedAccountCount >= REPORT_ACCOUNT_PAUSED_COUNT) {
            // 계정 1개월 정지
            NotifyParams params = createNotifyParams(report, REPORT_TITLE, REPORT_ACCOUNT_PAUSED_CONTENT, BAN_ONE_MONTH);
            notificationService.pushMessage(params);

            // 계정 상태 일시정지로 변경
            reportedAccount.updateAccountStatus(PAUSED);

            return ReportResponse.of(report, ReportMessage.REPORT_ACCOUNT_PAUSED);
        } else if (reportedAccountCount >= REPORT_ACCOUNT_WARNING_PUSH_COUNT) {
            // 경고 알림
            NotifyParams params = createNotifyParams(report, REPORT_TITLE, REPORT_ACCOUNT_WARING_CONTENT, REPORTED_ONCE);
            notificationService.pushMessage(params);
            return ReportResponse.of(report, ReportMessage.REPORT_ACCOUNT_WARNING);
        }
        return ReportResponse.of(report, ReportMessage.REPORT_SUCCESS);
    }

    public NotifyParams createNotifyParams(
            Report report,
            String title,
            String reportMessage,
            NotificationType notificationType
    ) {
        return NotifyParams.builder()
                .receiver(report.getReportedAccount())
                .redirectTargetId(report.getId())
                .title(title)
                .content(reportMessage)
                .type(notificationType)
                .build();
    }

    public List<ReportTypeResponse> getReportType() {
        return List.of(
                new ReportTypeResponse(ReportType.BAD_WORDS.toString(), ReportType.BAD_WORDS.getDetailContent()),
                new ReportTypeResponse(ReportType.FALSE_FACT.toString(), ReportType.FALSE_FACT.getDetailContent()),
                new ReportTypeResponse(ReportType.ADVERTISEMENT.toString(), ReportType.ADVERTISEMENT.getDetailContent()),
                new ReportTypeResponse(ReportType.ETC.toString(), ReportType.ETC.getDetailContent()));
    }

    public void adminReportAccount(
            Account loginAccount,
            Account reportedAccount,
            String content,
            NotificationType notificationType
    ) {
        Report report = Report.builder()
                .reporter(loginAccount)
                .reportedAccount(reportedAccount)
                .reportType(ReportType.ETC)
                .content(content)
                .build();
        reportRepository.save(report);
        NotificationDto.NotifyParams params = createNotifyParams(report, REPORT_TITLE_ADMIN, content, notificationType);
        notificationService.pushMessage(params);
    }
}
