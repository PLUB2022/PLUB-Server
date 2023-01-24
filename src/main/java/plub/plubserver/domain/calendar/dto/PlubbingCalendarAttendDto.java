package plub.plubserver.domain.calendar.dto;

import lombok.Builder;

import java.util.List;

public class PlubbingCalendarAttendDto {

    public record CalendarAttendResponse(
        Long calendarAttendId,
        String nickname,
        String profileImage,
        String AttendStatus
    ) {
        @Builder
        public CalendarAttendResponse{}
    }

    public record CalendarAttendList(
            List<CalendarAttendResponse> calendarAttendList
    ) {
        @Builder
        public CalendarAttendList{}
    }

    public record CheckAttendRequest(
        String attendStatus
    ) {
        @Builder
        public CheckAttendRequest {}
    }

}
