package plub.plubserver.domain.calendar.model;

import java.time.LocalDateTime;

public enum CalendarAlarmType {
    FIVE_MINUTES, FIFTEEN_MINUTES, THIRTY_MINUTES, ONE_HOUR, ONE_DAY, ONE_WEEK, NONE;

    public LocalDateTime getAlarmTime(LocalDateTime startTime) {
        switch (this) {
            case FIVE_MINUTES:
                return startTime.minusMinutes(5);
            case FIFTEEN_MINUTES:
                return startTime.minusMinutes(15);
            case THIRTY_MINUTES:
                return startTime.minusMinutes(30);
            case ONE_HOUR:
                return startTime.minusHours(1);
            case ONE_DAY:
                return startTime.minusDays(1);
            case ONE_WEEK:
                return startTime.minusWeeks(1);
            default:
                throw new IllegalArgumentException("Invalid alarm type");
        }
    }
}
