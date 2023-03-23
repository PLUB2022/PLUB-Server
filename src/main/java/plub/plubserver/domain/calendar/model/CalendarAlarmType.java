package plub.plubserver.domain.calendar.model;

import java.time.LocalDateTime;

public enum CalendarAlarmType {
    FIVE_MINUTES, FIFTEEN_MINUTES, THIRTY_MINUTES, ONE_HOUR, ONE_DAY, ONE_WEEK, NONE;

    public LocalDateTime getAlarmTime(LocalDateTime startedAt) {
        switch (this) {
            case FIVE_MINUTES:
                return startedAt.minusMinutes(5);
            case FIFTEEN_MINUTES:
                return startedAt.minusMinutes(15);
            case THIRTY_MINUTES:
                return startedAt.minusMinutes(30);
            case ONE_HOUR:
                return startedAt.minusHours(1);
            case ONE_DAY:
                return startedAt.minusDays(1);
            case ONE_WEEK:
                return startedAt.minusWeeks(1);
            default:
                throw new IllegalArgumentException("Invalid alarm type");
        }
    }
}
