package plub.plubserver.domain.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.AccountStatus;
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

import static plub.plubserver.domain.account.model.AccountStatus.*;
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
        Report report = reportRepository.save(request.toEntity(reporter, reportedAccount));
        return ReportIdResponse.of(report);
    }

    public Account checkReportTargetAccount(Long targetId, String reportTarget) {
        ReportTarget target = ReportTarget.toEnum(reportTarget);
        if (target == ReportTarget.ACCOUNT) {
            return accountRepository.findById(targetId).orElseThrow(() -> new AccountException(StatusCode.NOT_FOUND_ACCOUNT));
        } else if (target == ReportTarget.FEED) {
            return feedRepository.findById(targetId).orElseThrow(() -> new FeedException(StatusCode.NOT_FOUND_FEED))
                    .getAccount();
        } else if (target == ReportTarget.FEED_COMMENT) {
            return feedCommentRepository.findById(targetId).orElseThrow(() -> new FeedException(StatusCode.NOT_FOUND_COMMENT))
                    .getAccount();
        } else if (target == ReportTarget.NOTICE_COMMENT) {
            return noticeCommentRepository.findById(targetId).orElseThrow(() -> new NoticeException(StatusCode.NOT_FOUND_COMMENT))
                    .getAccount();
        } else if (target == ReportTarget.TODO) {
            return todoRepository.findById(targetId).orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO))
                    .getAccount();
        } else if (target == ReportTarget.ARCHIVE) {
            return archiveRepository.findById(targetId).orElseThrow(() -> new ArchiveException(StatusCode.NOT_FOUND_ARCHIVE))
                    .getAccount();
        } else if (target == ReportTarget.RECRUIT) {
            return recruitRepository.findById(targetId).orElseThrow(() -> new RecruitException(StatusCode.NOT_FOUND_RECRUIT))
                    .getPlubbing().getHost();
        } else {
            throw new ReportException(StatusCode.REPORT_TARGET_NOT_FOUND);
        }
    }

    public Long countReportedAccount(Account reportedAccount) {
        return reportRepository.countByReportedAccount(reportedAccount);
    }

    public ReportResponse notifyReportedAccount(Report report) {
        Account reportedAccount = report.getReportedAccount();
        Long reportedAccountCount = countReportedAccount(reportedAccount);
        if (reportedAccountCount >= REPORT_ACCOUNT_BAN_COUNT) {
            // 계정 영구 정지
            NotifyParams params = createNotifyParams(report, REPORT_ACCOUNT_BAN_CONTENT, BAN_PERMANENTLY);
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
        }
        else if (reportedAccountCount >= REPORT_ACCOUNT_PAUSED_COUNT) {
            // 계정 1개월 정지
            NotifyParams params = createNotifyParams(report, REPORT_ACCOUNT_PAUSED_CONTENT, BAN_ONE_MONTH);
             notificationService.pushMessage(params);

            // 계정 상태 일시정지로 변경
            reportedAccount.updateAccountStatus(PAUSED);

            return ReportResponse.of(report, ReportMessage.REPORT_ACCOUNT_PAUSED);
        }
        else if (reportedAccountCount >= REPORT_ACCOUNT_WARNING_PUSH_COUNT) {
            // 경고 알림
            NotifyParams params = createNotifyParams(report, REPORT_ACCOUNT_WARING_CONTENT, REPORTED_ONCE);
            notificationService.pushMessage(params);
            return ReportResponse.of(report, ReportMessage.REPORT_ACCOUNT_WARNING);
        }
        return ReportResponse.of(report, ReportMessage.REPORT_SUCCESS);
    }

    public NotifyParams createNotifyParams(
            Report report,
            String reportMessage,
            NotificationType notificationType
    ) {
        return NotifyParams.builder()
                .receiver(report.getReportedAccount())
                .redirectTargetId(report.getId())
                .title("신고")
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

    // 회원 영구 정지 해제
    @Transactional
    public void unsuspendAccount(Account account) {
        SuspendAccount suspendAccount = suspendAccountRepository.findByAccount(account)
                        .orElseThrow(() -> new ReportException(StatusCode.NOT_FOUND_SUSPEND_ACCOUNT));
        suspendAccount.setSuspended(false);
        suspendAccount.getAccount().updateAccountStatus(NORMAL);
    }

    // 회원 상태 변경
    @Transactional
    public void changeAccountStatus(Account loginAccount, Account reportedAccount, String status) {
        loginAccount.isAdmin();
        if (reportedAccount.getAccountStatus() == AccountStatus.PERMANENTLY_BANNED) {
            throw new ReportException(StatusCode.CANNOT_CHANGE_PERMANENTLY_BANNED_ACCOUNT);
        }
        else if(status.equals("PERMANENTLY_BANNED")) {
            SuspendAccount suspendAccount = SuspendAccount.builder()
                    .account(reportedAccount)
                    .isSuspended(true)
                    .build();
            suspendAccount.setSuspendedDate();
            suspendAccountRepository.save(suspendAccount);

            Report report = Report.builder()
                    .reporter(loginAccount)
                    .reportedAccount(reportedAccount)
                    .reportType(ReportType.ETC)
                    .content(ADMIN_REPORT_ACCOUNT_BAN)
                    .build();
            NotifyParams params = createNotifyParams(report, REPORT_ACCOUNT_BAN_CONTENT, BAN_PERMANENTLY);
            notificationService.pushMessage(params);
        }
        reportedAccount.updateAccountStatus(AccountStatus.valueOf(status));
    }

}
