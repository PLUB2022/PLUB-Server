package plub.plubserver.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static plub.plubserver.domain.notification.dto.NotificationDto.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    @Transactional
    public ReceivedAccountIdResponse send(NotificationRequest notificationRequest) {
        return new ReceivedAccountIdResponse(1L);
    }

    @Transactional
    public TotalReceivedAccountNumResponse sendAll() {
        return new TotalReceivedAccountNumResponse(100);
    }

    public NotificationListResponse getMyNotifications() {
        List<NotificationResponse> notifications = List.of(
                new NotificationResponse("title1", "body1", "2021-01-01", false),
                new NotificationResponse("title2", "body2", "2021-01-02", false),
                new NotificationResponse("title3", "body3", "2021-01-03", false)
        );
        return NotificationListResponse.of(notifications);
    }

    public String checkIsRead() {
        return "success";
    }

}
