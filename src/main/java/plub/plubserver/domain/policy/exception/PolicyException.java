package plub.plubserver.domain.policy.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class PolicyException extends PlubException {
    public PolicyException(StatusCode statusCode) {
        super(statusCode);
    }
}
