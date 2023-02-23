package plub.plubserver.domain.announcement.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class AnnouncementException extends PlubException {
    public AnnouncementException(StatusCode statusCode) {
        super(statusCode);
    }
}
