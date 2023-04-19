package plub.plubserver.domain.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.Role;
import plub.plubserver.domain.account.model.SuspendAccount;
import plub.plubserver.domain.account.repository.SuspendAccountRepository;
import plub.plubserver.domain.archive.model.Archive;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedComment;
import plub.plubserver.domain.notice.model.NoticeComment;
import plub.plubserver.domain.notification.dto.NotificationDto.NotifyParams;
import plub.plubserver.domain.notification.model.NotificationType;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.repository.PlubbingRepository;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.recruit.repository.BookmarkRepository;
import plub.plubserver.domain.report.config.ReportStatusMessage;
import plub.plubserver.domain.report.exception.ReportException;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.model.ReportTarget;
import plub.plubserver.domain.report.model.ReportType;
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

        // 신고 횟수가 2회 이상일 경우, 예외 발생 (어드민은 해당 X)
        if (count >= 2 && reporter.getRole().equals(Role.ROLE_USER)) {
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

    @Transactional
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
                .redirectTargetId(0L)
                .title(title)
                .content(reportMessage)
                .type(notificationType)
                .build();
    }

    public ReportTypeListResponse getReportType() {
        List<ReportTypeResponse> reportTypeResponses = List.of(
                new ReportTypeResponse(ReportType.BAD_WORDS.toString(), ReportType.BAD_WORDS.getDetailContent()),
                new ReportTypeResponse(ReportType.FALSE_FACT.toString(), ReportType.FALSE_FACT.getDetailContent()),
                new ReportTypeResponse(ReportType.ADVERTISEMENT.toString(), ReportType.ADVERTISEMENT.getDetailContent()),
                new ReportTypeResponse(ReportType.ETC.toString(), ReportType.ETC.getDetailContent()));
        return ReportTypeListResponse.of(reportTypeResponses);
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
