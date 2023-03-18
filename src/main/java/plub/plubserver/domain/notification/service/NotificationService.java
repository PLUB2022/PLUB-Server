package plub.plubserver.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.notification.model.Notification;

import java.util.concurrent.CompletableFuture;

import static plub.plubserver.domain.notification.dto.NotificationDto.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final FcmService fcmService;
    private final AccountService accountService;

    @Transactional
    public void pushMessage(NotifyParams params) {
        Account receiver = params.receiver();
        CompletableFuture<Boolean> future = fcmService.sendPushMessage(
                receiver.getFcmToken(),
                params.title(),
                params.content()
        );
        future.thenAccept(success -> {
            if (success) {
                Notification notification = Notification.builder()
                        .account(receiver)
                        .title(params.title())
                        .content(params.content())
                        .isRead(false)
                        .type(params.type())
                        .redirectTargetId(params.redirectTargetId())
                        .build();
                receiver.addNotification(notification);
            } else {
                log.warn("accountId={} push failed.", receiver.getId());
            }
        });
    }

    public NotificationListResponse getMyNotifications() {
        Account account = accountService.getCurrentAccount();
        return NotificationListResponse.of(account.getNotifications().stream()
                .map(NotificationResponse::of)
                .toList()
        );
    }
}
