package plub.plubserver.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.domain.notification.exception.NotificationException;
import plub.plubserver.domain.notification.model.Notification;

import static plub.plubserver.domain.notification.dto.NotificationDto.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final FcmService fcmService;
    private final AccountRepository accountRepository;

    @Transactional
    public void pushMessage(NotifyParams params) {
        Account receiver = accountRepository.findById(params.receiver().getId())
                .orElseThrow(() -> new NotificationException(StatusCode.NOT_FOUND_ACCOUNT));

        // 사용자가 알림 수신을 거부한 경우 바로 종료
        if (!receiver.isReceivedPushNotification()) return;

        // 원래 future로 비동기 fcm 송신을 하려고 했으나, thenAccept 이후 트랜잭션이 해당
        // 컨텍스트에서는 걸리지 않아서 future=true여도 저장이안됨, 그래서 그냥 동기로 처리
        // 단점 : FCM 메시지가 정상적으로 송신되지 않아도 디비에 notification이 저장 됨
        fcmService.sendPushMessage(
                receiver.getFcmToken(),
                params
        );
        Notification notification = Notification.builder()
                .account(receiver)
                .title(params.title())
                .content(params.content())
                .isRead(false)
                .type(params.type())
                .redirectTargetId(params.redirectTargetId())
                .build();
        receiver.addNotification(notification);
    }

    // FCM 송신 성공 여부와 상관없이 강제로 Notification 엔티티 저장 (테스트용)
    @Deprecated
    @Transactional
    public void pushMessageForceSave(NotifyParams params) {
        Account receiver = accountRepository.findById(params.receiver().getId())
                .orElseThrow(() -> new NotificationException(StatusCode.NOT_FOUND_ACCOUNT));
        fcmService.sendPushMessage(
                receiver.getFcmToken(),
                params
        );
        Notification notification = Notification.builder()
                .account(receiver)
                .title(params.title())
                .content(params.content())
                .isRead(false)
                .type(params.type())
                .redirectTargetId(params.redirectTargetId())
                .build();
        receiver.addNotification(notification);
    }

    public NotificationListResponse getMyNotifications(Account account) {
        Account loginAccount = accountRepository.findById(account.getId())
                .orElseThrow(() -> new NotificationException(StatusCode.NOT_FOUND_ACCOUNT));
        return NotificationListResponse.of(loginAccount.getNotifications().stream()
                .map(NotificationResponse::of)
                .sorted((n1, n2) -> n2.createdAt().compareTo(n1.createdAt()))
                .limit(30)
                .toList()
        );
    }

    @Transactional
    public NotificationResponse readNotification(Long notificationId, Account account) {
        Account loginAccount = accountRepository.findById(account.getId()).orElseThrow(
                () -> new NotificationException(StatusCode.NOT_FOUND_ACCOUNT)
        );
        Notification notification = loginAccount.getNotifications().stream()
                .filter(n -> n.getId().equals(notificationId))
                .findFirst()
                .orElseThrow(() -> new NotificationException(StatusCode.NOT_FOUND_NOTIFICATION));
        notification.read();
        return NotificationResponse.of(notification);
    }
}
