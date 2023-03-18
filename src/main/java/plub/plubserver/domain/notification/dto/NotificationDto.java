package plub.plubserver.domain.notification.dto;

import lombok.Builder;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.notification.model.Notification;
import plub.plubserver.domain.notification.model.NotificationType;

import java.util.List;

public class NotificationDto {
    public record NotifyParams(
            Account receiver,
            NotificationType type,
            Long redirectTargetId,
            String title,
            String content
    ) {
        @Builder public NotifyParams {
        }
    }

    /**
     * Request
     */
    public record DirectPushRequest(
            Long accountId,
            String title,
            String content
    ) { }

    public record PlubbingPushRequest(
            Long plubbingId,
            String title,
            String content
    ) { }


    /**
     * Response
     */
    public record ReceivedAccountIdResponse(
            Long accountId
    ) {
    }

    public record ReceivedAccountsResponse(
            List<Long> accountIds,
            int count
    ) {
        @Builder public ReceivedAccountsResponse {
        }
    }
    
    public record NotificationResponse(
            String title,
            String body,
            String createdAt,
            boolean isRead
    ) {
        @Builder public NotificationResponse {
        }
        
        public static NotificationResponse of(Notification notification) {
            return NotificationResponse.builder()
                    .title(notification.getTitle())
                    .body(notification.getContent())
                    .createdAt(notification.getCreatedAt())
                    .isRead(true)
                    .build();
            
        }
    }
    
    public record NotificationListResponse(
            List<NotificationResponse> notifications
    ) {
        @Builder public NotificationListResponse {
        }

        public static NotificationListResponse of(List<NotificationResponse> notifications) {
            return NotificationListResponse.builder()
                    .notifications(notifications)
                    .build();
        }
    }
}
