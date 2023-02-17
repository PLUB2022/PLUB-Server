package plub.plubserver.domain.notification.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class NotificationException extends PlubException {

    public NotificationException(StatusCode statusCode) {
        super(statusCode);
    }
}
