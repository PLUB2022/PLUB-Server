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
     * Response
     */
    public record NotificationResponse(
            Long notificationId,
            NotificationType notificationType,
            String targetEntity,
            Long redirectTargetId,
            String title,
            String body,
            String createdAt,
            boolean isRead
    ) {
        @Builder public NotificationResponse {
        }
        
        public static NotificationResponse of(Notification notification) {
            NotificationType notificationType = notification.getType();
            String targetClassName = notificationType.redirectTargetClass().getSimpleName();
            return NotificationResponse.builder()
                    .notificationId(notification.getId())
                    .notificationType(notificationType)
                    .targetEntity(targetClassName)
                    .redirectTargetId(notification.getRedirectTargetId())
                    .title(notification.getTitle())
                    .body(notification.getContent())
                    .createdAt(notification.getCreatedAt())
                    .isRead(notification.isRead())
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
