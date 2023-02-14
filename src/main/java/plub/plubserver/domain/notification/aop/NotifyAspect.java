package plub.plubserver.domain.notification.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;
@Deprecated
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class NotifyAspect {

    private final AccountService accountService;
    private final PlubbingService plubbingService;
    private final NotificationService notificationService;

    @AfterReturning("@annotation(plub.plubserver.domain.notification.aop.NotifyHost) && args(plubbingId)")
    public void notifyHost(JoinPoint joinPoint, Long plubbingId) {
        log.info("notifyHost 호출 됨.");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        NotifyHost notify = signature.getMethod().getAnnotation(NotifyHost.class);
        Account host = plubbingService.getPlubbing(plubbingId).getHost();
        String title = "";
        String content = "";

        if (notify.detail() == NotifyDetail.APPLY_RECRUIT) {
            title = "APPLY_RECRUIT";
            content = "APPLY_RECRUIT";
        }

        if (notify.detail() == NotifyDetail.LEAVE_PLUBBING) {
            title = "LEAVE_PLUBBING";
            content = "LEAVE_PLUBBING";

        }

        notificationService.pushMessage(host, title, content);
        log.info("푸시 알림 전송(notifyHost) - host={}, title={}, content={}",
                host.getId(), title, content);
        log.info("{}", plubbingId);
    }

    @AfterReturning("@annotation(plub.plubserver.domain.notification.aop.NotifyPlubbingMembers) && args(plubbingId)")
    public void notifyPlubbingMembers(JoinPoint joinPoint, Long plubbingId) {
        log.info("notifyPlubbingMembers 호출 됨.");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        NotifyPlubbingMembers notify = signature.getMethod().getAnnotation(NotifyPlubbingMembers.class);
        String title = "";
        String content = "";

        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);

        if (notify.detail() == NotifyDetail.NEW_CALENDAR) {
            title = "NEW_PLUBBING_CALENDAR";
            content = "NEW_PLUBBING_CALENDAR";
        }

        if (notify.detail() == NotifyDetail.UPDATED_CALENDAR) {
            title = "UPDATED_PLUBBING_CALENDAR";
            content = "UPDATED_PLUBBING_CALENDAR";
        }

        if (notify.detail() == NotifyDetail.NEW_NOTICE) {
            title = plubbing.getName() + "에 새로운 공지가 등록되었어요.";
            content = "UPDATED_PLUBBING_CALENDAR";
        }

        final String finalTitle = title;
        final String finalContent = content;

        plubbing.getMembers().forEach(member -> {
            notificationService.pushMessage(member, finalTitle, finalContent);
            log.info("푸시 알림 전송(notifyPlubbingMembers) - member={}, title={}, content={}",
                    member.getId(), finalTitle, finalContent);
        });
        log.info("{}", plubbingId);
    }

    @AfterReturning("@annotation(plub.plubserver.domain.notification.aop.NotifyAccount) && args(plubbingId)")
    public void notifyAccount(JoinPoint joinPoint, Long plubbingId) {
        log.info("notifyAccount 호출 됨.");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        NotifyAccount notify = signature.getMethod().getAnnotation(NotifyAccount.class);
        String title = "";
        String content = "";

        if (notify.detail() == NotifyDetail.NEW_FEED_COMMENT) {
            title = "NEW_FEED_COMMENT";
            content = "NEW_FEED_COMMENT";
        }

        if (notify.detail() == NotifyDetail.NEW_NOTICE_COMMENT) {
            title = "NEW_NOTICE_COMMENT";
            content = "NEW_NOTICE_COMMENT";

        }

        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
    }

}
