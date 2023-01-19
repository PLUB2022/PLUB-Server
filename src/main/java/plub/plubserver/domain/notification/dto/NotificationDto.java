package plub.plubserver.domain.notification.dto;

import lombok.Builder;
import plub.plubserver.domain.notification.model.Notification;

import java.util.List;

public class NotificationDto {
    /**
     * Request
     */
    public record NotificationRequest(
            String targetToken,
            String title,
            String body
    ) { }


    /**
     * Response
     */
    public record ReceivedAccountIdResponse(
            Long accountId
    ) {
    }

    public record TotalReceivedAccountNumResponse(
            int totalReceivedAccountNum
    ) {
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
                    .body(notification.getBody())
                    .createdAt(notification.getCreatedAt())
                    .isRead(false) // TODO : 읽음 여부 체크
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
