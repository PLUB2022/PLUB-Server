package plub.plubserver.domain.calendar.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class CalendarException extends PlubException {

    public CalendarException(StatusCode statusCode) {
        super(statusCode);
    }
}