package plub.plubserver.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.notification.model.Notification;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import java.util.concurrent.CompletableFuture;

import static plub.plubserver.domain.notification.dto.NotificationDto.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final FcmService fcmService;
    private final AccountService accountService;
    private final PlubbingService plubbingService;

    private void pushMessage(Account receiver, String title, String content) {
        CompletableFuture<Boolean> future = fcmService.sendPushMessage(
                receiver.getFcmToken(),
                title,
                content
        );
        future.thenAccept(success -> {
            if (success) {
                createNotification(receiver, title, content);
            } else {
                log.warn("accountId={} push failed.", receiver.getId());
            }
        });
    }

    @Transactional
    public void createNotification(Account account, String title, String content) {
        Notification notification = Notification.builder()
                .account(account)
                .title(title)
                .content(content)
                .isRead(false)
                .build();
        account.addNotification(notification);
    }

    @Transactional
    public ReceivedAccountIdResponse sendDirect(DirectPushRequest directPushRequest) {
        Account receiver = accountService.getAccount(directPushRequest.accountId());
        pushMessage(receiver, directPushRequest.title(), directPushRequest.content());
        return new ReceivedAccountIdResponse(directPushRequest.accountId());
    }

    @Transactional
    public ReceivedAccountsResponse sendToPlubbing(PlubbingPushRequest plubbingPushRequest) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingPushRequest.plubbingId());
//        List<Long> receivedIds = plubbing.getAccountPlubbingList().stream()
//                .map(it -> pushMessage(
//                        it.getAccount(),
//                        plubbingPushRequest.title(),
//                        plubbingPushRequest.content()
//                        ))
//                .filter(Objects::nonNull)
//                .toList();
//        List<Long> receivedIds = plubbing.getAccountPlubbingList().stream()
//                .map(it -> pushMessage(
//                        it.getAccount(),
//                        plubbingPushRequest.title(),
//                        plubbingPushRequest.content()
//                        ))
//                .toList();
//        return ReceivedAccountsResponse.builder()
//                .accountIds(receivedIds)
//                .count(receivedIds.size())
//                .build();
        return null;
    }

    @Transactional
    public ReceivedAccountsResponse sendToAll() {
        for (int i = 0 ; i<100_000; i++) {
            fcmService.sendPushMessage(
                    "fcmToken",
                    "title",
                    "content");
        }
        return null;
    }

    public NotificationListResponse getMyNotifications() {
        Account account = accountService.getCurrentAccount();
//        List<NotificationResponse> notifications = List.of(
//                new NotificationResponse("title1", "body1", "2021-01-01", false),
//                new NotificationResponse("title2", "body2", "2021-01-02", false),
//                new NotificationResponse("title3", "body3", "2021-01-03", false)
//        );
        return NotificationListResponse.of(account.getNotifications().stream()
                .map(NotificationResponse::of)
                .toList()
        );
    }
}
