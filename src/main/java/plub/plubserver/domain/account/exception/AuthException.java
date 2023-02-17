package plub.plubserver.domain.account.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

import java.util.HashMap;

public class AuthException extends PlubException {
    public Object data;

    public AuthException(StatusCode statusCode) {
        super(statusCode);
        this.data = new HashMap<String, String>();
    }

    public AuthException(StatusCode statusCode, String message) {
        super(statusCode, message);
        this.data = new HashMap<String, String>();
    }

    public AuthException(StatusCode statusCode, Object data) {
        super(statusCode);
        this.data = data;
    }

}
