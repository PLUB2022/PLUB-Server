package plub.plubserver.domain.calendar.exception;

import plub.plubserver.domain.calendar.config.CalendarCode;

public class CalendarException extends RuntimeException {
    CalendarCode calendarCode;

    public CalendarException(CalendarCode calendarCode) {
        super(calendarCode.getMessage());
        this.calendarCode = calendarCode;
    }
}