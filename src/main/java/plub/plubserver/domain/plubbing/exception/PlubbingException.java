package plub.plubserver.domain.plubbing.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class PlubbingException extends PlubException {

    public PlubbingException(StatusCode statusCode) {
        super(statusCode);
    }
}
