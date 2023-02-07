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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static plub.plubserver.domain.notification.dto.NotificationDto.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final FcmService fcmService;
    private final AccountService accountService;
    private final PlubbingService plubbingService;

    private Long pushMessage(Account receiver, String title, String content) {
        CompletableFuture<Boolean> result = fcmService.sendPushMessage(
                receiver.getFcmToken(),
                title,
                content
        );
        try {
            if (result.isDone() && result.get()) {
                Notification notification = Notification.builder()
                        .account(receiver)
                        .title(title)
                        .content(content)
                        .isRead(false)
                        .build();
                receiver.addNotification(notification);
                return receiver.getId();
            } else {
                log.warn("accountId:{} push failed.", receiver.getId());
            }
        } catch (ExecutionException | InterruptedException e) {
            log.warn("accountId:{} push failed. detail {}", receiver.getId(), e.getMessage());
        }
        return null;
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
        List<Long> receivedIds = plubbing.getAccountPlubbingList().stream()
                .map(it -> pushMessage(
                        it.getAccount(),
                        plubbingPushRequest.title(),
                        plubbingPushRequest.content()
                        ))
                .filter(Objects::nonNull)
                .toList();
        return ReceivedAccountsResponse.builder()
                .accountIds(receivedIds)
                .count(receivedIds.size())
                .build();
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

    public String checkIsRead() {
        return "success";
    }

}
