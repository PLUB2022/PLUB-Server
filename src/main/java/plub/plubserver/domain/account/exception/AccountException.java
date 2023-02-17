package plub.plubserver.domain.account.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class AccountException extends PlubException {

    public AccountException(StatusCode statusCode) {
        super(statusCode);
    }
}
