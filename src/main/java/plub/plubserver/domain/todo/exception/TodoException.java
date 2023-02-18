package plub.plubserver.domain.todo.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class TodoException extends PlubException {

    public TodoException(StatusCode statusCode) {
        super(statusCode);
    }
}
