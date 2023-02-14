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
        Notify notify = signature.getMethod().getAnnotation(Notify.class);
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
        Notify notify = signature.getMethod().getAnnotation(Notify.class);
        String title = "";
        String content = "";

        if (notify.detail() == NotifyDetail.NEW_PLUBBING_CALENDAR) {
            title = "NEW_PLUBBING_CALENDAR";
            content = "NEW_PLUBBING_CALENDAR";
        }

        if (notify.detail() == NotifyDetail.UPDATED_PLUBBING_CALENDAR) {
            title = "UPDATED_PLUBBING_CALENDAR";
            content = "UPDATED_PLUBBING_CALENDAR";

        }

        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        final String finalTitle = title;
        final String finalContent = content;

        plubbing.getMembers().forEach(member -> {
            notificationService.pushMessage(member, finalTitle, finalContent);
            log.info("푸시 알림 전송(notifyPlubbingMembers) - member={}, title={}, content={}",
                    member.getId(), finalTitle, finalContent);
        });
        log.info("{}", plubbingId);
    }

//    @AfterReturning("@annotation(plub.plubserver.domain.notification.aop.NotifyHost) && args(plubbingId)")
//    public void notify(JoinPoint joinPoint, Long plubbingId) {
//        log.info("NotifyAspect 호출 됨.");
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Notify notify = signature.getMethod().getAnnotation(Notify.class);
//        switch(signature.getMethod().getAnnotation(Notify.class).who()) {
//            case ACCOUNT -> notifyAccount(notify, accountId);
//            case PLUBBING_MEMBERS -> notifyPlubbingMembers(notify, plubbingId);
//            case HOST -> notifyHost(notify, plubbingId);
//        }
//    }

    // 내 게시글에 댓글, -> account 객체를 받음
    // 지원 모임 수락, -> 가능
    // to-do 리마인더 -> 스케줄러라 따로 뺴야할듯...
    private void notifyAccount(Notify notify, Long accountId) {
        Account receiver = accountService.getAccount(accountId);
        String title = "";
        String content = "";

        if (notify.detail() == NotifyDetail.ACCEPT_RECRUIT) {
            title = "ACCEPT_RECRUIT";
            content = "ACCEPT_RECRUIT";
        }

        if (notify.detail() == NotifyDetail.TO_DO_REMINDER) {
            title = "LEAVE_PLUBBING";
            content = "LEAVE_PLUBBING";
            receiver = accountService.getCurrentAccount();
        }

        if (notify.detail() == NotifyDetail.NEW_PLUBBING_FEED_COMMENT) {
            title = "LEAVE_PLUBBING";
            content = "LEAVE_PLUBBING";
        }

        notificationService.pushMessage(receiver, title, content);
        log.info("푸시 알림 전송(notifyHost) - host={}, title={}, content={}",
                receiver.getId(), title, content);
    }

}
