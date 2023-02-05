package plub.plubserver.domain.calendar.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CalendarCode {
    NOT_FOUNT_CALENDAR(404, 7010, "not fount calendar"),
    NOT_FOUNT_CALENDAR_ATTEND(404, 7011, "not fount calendar attend"),
    ;

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
