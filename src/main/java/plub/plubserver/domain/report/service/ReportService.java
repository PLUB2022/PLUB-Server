package plub.plubserver.domain.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.exception.PlubException;
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
import plub.plubserver.domain.notification.dto.NotificationDto.NotifyParams;
import plub.plubserver.domain.notification.model.NotificationType;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.repository.PlubbingRepository;
import plub.plubserver.domain.recruit.exception.RecruitException;
import plub.plubserver.domain.recruit.repository.RecruitRepository;
import plub.plubserver.domain.report.config.ReportStatusMessage;
import plub.plubserver.domain.report.dto.ReportDto.ReportResponse;
import plub.plubserver.domain.report.dto.ReportDto.ReportTypeResponse;
import plub.plubserver.domain.report.exception.ReportException;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.model.ReportTarget;
import plub.plubserver.domain.report.model.ReportType;
import plub.plubserver.domain.report.repositoy.ReportRepository;
import plub.plubserver.domain.todo.exception.TodoException;
import plub.plubserver.domain.todo.repository.TodoRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static plub.plubserver.domain.account.model.AccountStatus.PAUSED;
import static plub.plubserver.domain.account.model.AccountStatus.PERMANENTLY_BANNED;
import static plub.plubserver.domain.notification.model.NotificationType.*;
import static plub.plubserver.domain.report.config.ReportConstant.*;
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
    private final PlubbingRepository plubbingRepository;


    // 신고하기
    @Transactional
    public ReportIdResponse createReport(CreateReportRequest request, Account reporter) {
        Account reportedAccount = checkReportTargetAccount(request.reportTargetId(), request.reportTarget());
        plubbingRepository.findById(request.plubbingId())
                .orElseThrow(() -> new PlubException(StatusCode.NOT_FOUND_PLUBBING));

        // 해당 유저가 최근 7일간에 신고한 기록이 있는지 확인
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(7);

        List<Report> reports = reportRepository.findAllByReporter(reporter);
        long count = reports.stream()
                .filter(r -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime createdAt = LocalDateTime.parse(r.getCreatedAt(), formatter);
                    return createdAt.isAfter(weekAgo);
                })
                .count();

        // 신고 횟수가 2회 이상일 경우, 예외 발생
        if (count >= 2) {
            throw new ReportException(StatusCode.TOO_MANY_REPORTS);
        }

        Report createReport = request.toEntity(reporter, reportedAccount, request.plubbingId());
        checkDuplicateReport(createReport);
        Report report = reportRepository.save(createReport);
        ReportStatusMessage reportStatusMessage = checkReportFrequency(createReport.getReportedAccount());
        createReport.setReportStatusMessage(reportStatusMessage);
        notifyReportedAccount(report);

        return ReportIdResponse.of(report);
    }

    // 신고 조회
    public ReportResponse getReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportException(StatusCode.NOT_FOUND_REPORT));
        ReportStatusMessage reportStatusMessage = report.getReportStatusMessage();
        String nickname = report.getReportedAccount().getNickname();
        String reason = report.getReportReason();
        String plubbingName = checkPlubbingName(report.getPlubbingId());
        String title = reportStatusMessage.toTitle(nickname);
        String content = reportStatusMessage.toContent(nickname, reason, plubbingName);
        return ReportResponse.of(report, title, content);
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
        return switch (target) {
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
        };
    }


    public Long countReportedAccount(Account reportedAccount) {
        return reportRepository.countByReportedAccountAndCheckCanceledFalse(reportedAccount);
    }

    private String checkPlubbingName(Long plubbingId) {
        Optional<Plubbing> plubbing = plubbingRepository.findById(plubbingId);
        String plubbingName;
        if (plubbing.isEmpty()) {
            plubbingName = "알 수 없음";
        } else {
            plubbingName = plubbing.get().getName();
        }
        return plubbingName;
    }

    public void notifyReportedAccount(Report report) {
        Account reportedAccount = report.getReportedAccount();
        Long reportedAccountCount = countReportedAccount(reportedAccount);
        String nickname = reportedAccount.getNickname();
        if (reportedAccountCount >= REPORT_ACCOUNT_BAN_COUNT) {
            // 계정 영구 정지
            NotifyParams params = createNotifyParams(
                    report,
                    ReportStatusMessage.PERMANENTLY_BANNED.getReportFCMTitle(),
                    ReportStatusMessage.PERMANENTLY_BANNED.toFCMContent(nickname),
                    BAN_PERMANENTLY
            );
            notificationService.pushMessage(params);
            reportedAccount.updateAccountStatus(PERMANENTLY_BANNED);

            // 디비에 영구 계정 등록
            SuspendAccount suspendAccount = SuspendAccount.builder()
                    .accountId(reportedAccount.getId())
                    .accountEmail(reportedAccount.getEmail())
                    .accountDI(reportedAccount.getEmail().split("@")[0])
                    .isSuspended(true)
                    .build();
            suspendAccount.setSuspendedDate();
            suspendAccountRepository.save(suspendAccount);
        } else if (reportedAccountCount >= REPORT_ACCOUNT_PAUSED_COUNT) {
            // 계정 1개월 정지
            NotifyParams params = createNotifyParams(
                    report,
                    ReportStatusMessage.PAUSED.getReportFCMTitle(),
                    ReportStatusMessage.PAUSED.toFCMContent(nickname),
                    BAN_ONE_MONTH
            );
            notificationService.pushMessage(params);

            // 계정 상태 일시정지로 변경
            reportedAccount.updateAccountStatus(PAUSED);
            reportedAccount.setPausedDate();
        } else if (reportedAccountCount >= REPORT_ACCOUNT_WARNING_PUSH_COUNT) {
            // 경고 알림
            NotifyParams params = createNotifyParams(
                    report,
                    ReportStatusMessage.WARNING.getReportFCMTitle(),
                    ReportStatusMessage.WARNING.toFCMContent(nickname),
                    REPORTED_ONCE
            );
            notificationService.pushMessage(params);
        }
    }

    public ReportStatusMessage checkReportFrequency(Account reportedAccount) {
        Long frequency = countReportedAccount(reportedAccount);
        if (frequency >= REPORT_ACCOUNT_BAN_COUNT) {
            return ReportStatusMessage.PERMANENTLY_BANNED;
        } else if (frequency >= REPORT_ACCOUNT_PAUSED_COUNT) {
            return ReportStatusMessage.PAUSED;
        } else {
            return ReportStatusMessage.WARNING;
        }
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
            ReportStatusMessage reportStatusMessage,
            NotificationType notificationType
    ) {
        Report report = Report.builder()
                .reportType(ReportType.ETC)
                .reportTarget(ReportTarget.ACCOUNT)
                .targetId(reportedAccount.getId())
                .reportReason(content)
                .plubbingId(reportedAccount.getAccountPlubbingList().get(0).getPlubbing().getId())
                .reporter(loginAccount)
                .reportedAccount(reportedAccount)
                .reportStatusMessage(reportStatusMessage)
                .checkCanceled(true)
                .build();
        reportRepository.save(report);

        NotifyParams params = createNotifyParams(
                report,
                reportStatusMessage.getReportFCMTitle(),
                reportStatusMessage.toFCMContent(reportedAccount.getNickname()),
                notificationType
        );
        notificationService.pushMessage(params);
    }

    // 신고 취소 처리
    @Transactional
    public ReportIdResponse cancelReport(Long reportId, boolean isCancel, Account loginAccount) {
        loginAccount.isAdmin();
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportException(StatusCode.NOT_FOUND_REPORT));
        report.cancelReport(isCancel);
        return ReportIdResponse.of(report);
    }
}
