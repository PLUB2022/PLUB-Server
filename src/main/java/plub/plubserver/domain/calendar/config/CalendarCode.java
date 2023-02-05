package plub.plubserver.domain.calendar.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CalendarCode {
    NOT_FOUNT_CALENDAR(404, 7010, "not fount calendar"),
    ;

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
