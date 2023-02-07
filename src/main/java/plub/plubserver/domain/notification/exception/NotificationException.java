package plub.plubserver.domain.notification.exception;

import plub.plubserver.domain.notification.config.NotificationCode;

public class NotificationException extends RuntimeException {
    NotificationCode notificationCode;
    public NotificationException(NotificationCode notificationCode) {
        super(notificationCode.getMessage());
        this.notificationCode = notificationCode;
    }
}
