package plub.plubserver.domain.notice.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class NoticeException extends PlubException {

    public NoticeException(StatusCode statusCode) {
        super(statusCode);
    }
}
