package plub.plubserver.domain.notification.exception;

import plub.plubserver.domain.notification.config.NotificationCode;

public class NotificationException extends RuntimeException {
    public NotificationCode notificationCode;
    public NotificationException(NotificationCode notificationCode) {
        super(notificationCode.getMessage());
        this.notificationCode = notificationCode;
    }
}
