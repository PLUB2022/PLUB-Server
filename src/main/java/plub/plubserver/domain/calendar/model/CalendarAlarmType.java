package plub.plubserver.domain.calendar.model;

import java.time.LocalDateTime;

public enum CalendarAlarmType {
    FIVE_MINUTES, FIFTEEN_MINUTES, THIRTY_MINUTES, ONE_HOUR, ONE_DAY, ONE_WEEK, NONE;

    public LocalDateTime getAlarmTime(LocalDateTime startTime) {
        switch (this) {
            case FIVE_MINUTES:
                return startTime.plusMinutes(5);
            case FIFTEEN_MINUTES:
                return startTime.plusMinutes(15);
            case THIRTY_MINUTES:
                return startTime.plusMinutes(30);
            case ONE_HOUR:
                return startTime.plusHours(1);
            case ONE_DAY:
                return startTime.plusDays(1);
            case ONE_WEEK:
                return startTime.plusWeeks(1);
            default:
                throw new IllegalArgumentException("Invalid alarm type");
        }
    }
}
