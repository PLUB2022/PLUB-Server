package plub.plubserver.domain.feed.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class FeedException extends PlubException {
    public FeedException(StatusCode statusCode) {
        super(statusCode);
    }
}
