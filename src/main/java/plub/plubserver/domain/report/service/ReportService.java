package plub.plubserver.domain.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.SuspendAccount;
import plub.plubserver.domain.account.repository.SuspendAccountRepository;
import plub.plubserver.domain.archive.model.Archive;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedComment;
import plub.plubserver.domain.notice.model.NoticeComment;
import plub.plubserver.domain.notification.dto.NotificationDto.NotifyParams;
import plub.plubserver.domain.notification.model.NotificationType;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.repository.PlubbingRepository;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.recruit.repository.BookmarkRepository;
import plub.plubserver.domain.report.config.ReportStatusMessage;
import plub.plubserver.domain.report.exception.ReportException;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.model.ReportTarget;
import plub.plubserver.domain.report.repositoy.ReportRepository;
import plub.plubserver.domain.todo.model.Todo;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static plub.plubserver.common.constant.GlobalConstants.*;
import static plub.plubserver.domain.account.model.AccountStatus.PAUSED;
import static plub.plubserver.domain.account.model.AccountStatus.PERMANENTLY_BANNED;
import static plub.plubserver.domain.notification.model.NotificationType.*;
import static plub.plubserver.domain.report.dto.ReportDto.*;
import static plub.plubserver.domain.report.model.ReportType.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final NotificationService notificationService;
    private final SuspendAccountRepository suspendAccountRepository;
    private final PlubbingRepository plubbingRepository;
    private final BookmarkRepository bookmarkRepository;
    private final EntityManager em;

    // 신고하기
    @Transactional
    public ReportIdResponse createReport(CreateReportRequest request, Account reporter) {
        Account reportedAccount = getReportTargetAccount(request.reportTargetId(), request.reportTarget());
        plubbingRepository.findById(request.plubbingId())
                .orElseThrow(() -> new PlubException(StatusCode.NOT_FOUND_PLUBBING));

        Report createReport = request.toEntity(reporter, reportedAccount, request.plubbingId());
        checkDuplicateReport(createReport);
        Report report = reportRepository.save(createReport);
        checkReportFrequency(report);
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

    private <T> T findOrThrow(Class<T> targetClass, Long id) {
        return Optional.ofNullable(em.find(targetClass, id)).orElseThrow(
                () -> new ReportException(StatusCode.REPORT_TARGET_NOT_FOUND)
        );
    }

    public Account getReportTargetAccount(Long targetId, String reportTarget) {
        ReportTarget target = ReportTarget.toEnum(reportTarget);
        return switch (target) {
            case ACCOUNT -> findOrThrow(Account.class, targetId);
            case FEED -> findOrThrow(Feed.class, targetId)
                    .getAccount();
            case FEED_COMMENT -> findOrThrow(FeedComment.class, targetId)
                    .getAccount();
            case NOTICE_COMMENT -> findOrThrow(NoticeComment.class, targetId)
                    .getAccount();
            case TODO -> findOrThrow(Todo.class, targetId)
                    .getAccount();
            case ARCHIVE -> findOrThrow(Archive.class, targetId)
                    .getAccount();
            case RECRUIT -> findOrThrow(Recruit.class, targetId)
                    .getPlubbing().getHost();
        };
    }


    // 누적 신고 수 확인
    public Long countTarget(Report report) {
        return reportRepository
                .countByTargetIdAndReportTypeAndCheckCanceledFalse(report.getTargetId(), report.getReportTarget());
    }

    // 최근 신고 수 확인
    public Long countTargetWithRecentDateTime(Report report) {
        List<Report> reportList = reportRepository
                .findAllByTargetIdAndReportTypeAndCheckCanceledFalse(report.getTargetId(), report.getReportTarget());

        return reportList.stream()
                .filter(r -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime checkDateTime = LocalDateTime.parse(r.getModifiedAt(), formatter);
                    if (report.getReportTarget() == ReportTarget.ACCOUNT || report.getReportTarget() == ReportTarget.FEED_COMMENT || report.getReportTarget() == ReportTarget.NOTICE_COMMENT) {
                        return checkDateTime.isAfter(LocalDateTime.now().minusHours(6));
                    } else {
                        return checkDateTime.isAfter(LocalDateTime.now().minusHours(12));
                    }
                }).count();
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

    @Transactional
    public void notifyReportedAccount(Report report) {
        Account reportedAccount = report.getReportedAccount();
        int reportedAccountCount = reportedAccount.getReportCount();
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
                    .checkSuspended(true)
                    .build();
            suspendAccount.setSuspendedDate();
            suspendAccountRepository.save(suspendAccount);

            // 북마크 전체 삭제 후 모임 상태 정지로 변경
            plubbingRepository.findAllByHost(reportedAccount).forEach(plubbing -> {
                bookmarkRepository.deleteByRecruit(plubbing.getRecruit());
                plubbing.pause();
            });
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

    @Transactional
    public void checkReportFrequency(Report report) {
        switch (report.getReportTarget()) {
            case RECRUIT -> handleDeletableReport(
                    report, Recruit.class,
                    RECRUIT_CHECK_FREQUENCY, RECRUIT_CHECK_RECENT_FREQUENCY
            );
            case FEED -> handleDeletableReport(
                    report, Feed.class,
                    FEED_CHECK_FREQUENCY, FEED_CHECK_RECENT_FREQUENCY
            );
            case TODO -> handleDeletableReport(
                    report, Todo.class,
                    TODO_CHECK_FREQUENCY, TODO_CHECK_RECENT_FREQUENCY
            );
            case ARCHIVE -> handleDeletableReport(
                    report, Archive.class,
                    ARCHIVE_CHECK_FREQUENCY, ARCHIVE_CHECK_RECENT_FREQUENCY
            );
            case FEED_COMMENT -> handleDeletableReport(
                    report, FeedComment.class,
                    FEED_COMMENT_CHECK_FREQUENCY, FEED_COMMENT_CHECK_RECENT_FREQUENCY
            );
            case NOTICE_COMMENT -> handleDeletableReport(
                    report, NoticeComment.class,
                    NOTICE_COMMENT_CHECK_FREQUENCY, NOTICE_COMMENT_CHECK_RECENT_FREQUENCY
            );
            case ACCOUNT -> handleAccountReport(report);
        }
    }

    private <T extends BaseEntity> void handleDeletableReport(
            Report report,
            Class<T> clazz,
            int maxFrequency,
            int maxRecentFrequency
    ) {
        Long frequency = countTarget(report);
        Long recentFrequency = countTargetWithRecentDateTime(report);
        T entity = findOrThrow(clazz, report.getTargetId());
        if (frequency >= maxFrequency || recentFrequency >= maxRecentFrequency) {
            entity.softDelete();
        }
    }

    private void handleAccountReport(Report report) {
        Account account = findOrThrow(Account.class, report.getTargetId());
        Long frequency = countTarget(report);
        Long recentFrequency = countTargetWithRecentDateTime(report);
        if (frequency >= ACCOUNT_CHECK_FREQUENCY || recentFrequency >= ACCOUNT_CHECK_RECENT_FREQUENCY) {
            // 일시 정지
            account.updateAccountStatus(PAUSED);
            account.plusReportCount();
            // 알림
            report.setReportStatusMessage(ReportStatusMessage.PAUSED);
            notifyReportedAccount(report);
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
                .redirectTargetId(0L)
                .title(title)
                .content(reportMessage)
                .type(notificationType)
                .build();
    }

    public ReportTypeListResponse getReportType() {
        List<ReportTypeResponse> reportTypeResponses = List.of(
                new ReportTypeResponse(BAD_WORDS.toString(), BAD_WORDS.getDetailContent()),
                new ReportTypeResponse(FALSE_FACT.toString(), FALSE_FACT.getDetailContent()),
                new ReportTypeResponse(ADVERTISEMENT.toString(), ADVERTISEMENT.getDetailContent()),
                new ReportTypeResponse(ETC.toString(), ETC.getDetailContent()));
        return ReportTypeListResponse.of(reportTypeResponses);
    }

    public void adminReportAccount(
            Account loginAccount,
            Account reportedAccount,
            String content,
            ReportStatusMessage reportStatusMessage,
            NotificationType notificationType
    ) {
        Optional<AccountPlubbing> plubbingOpt = reportedAccount.getAccountPlubbingList().stream()
                .filter(accountPlubbing -> accountPlubbing != null && accountPlubbing.getPlubbing() != null)
                .findFirst();

        if (plubbingOpt.isPresent()) {
            AccountPlubbing accountPlubbing = plubbingOpt.get();
            Long plubbingId = accountPlubbing.getPlubbing().getId();
            Report report = Report.builder()
                    .reportType(ETC)
                    .reportTarget(ReportTarget.ACCOUNT)
                    .targetId(reportedAccount.getId())
                    .reportReason(content)
                    .plubbingId(plubbingId)
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
