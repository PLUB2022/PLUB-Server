package plub.plubserver.domain.calendar;

import plub.plubserver.domain.calendar.model.Calendar;

import static plub.plubserver.domain.calendar.dto.CalendarAttendDto.CheckAttendRequest;
import static plub.plubserver.domain.calendar.dto.CalendarDto.CreateCalendarRequest;
import static plub.plubserver.domain.calendar.dto.CalendarDto.UpdateCalendarRequest;

public class CalendarMockUtils {
    public static CreateCalendarRequest createCalendarRequest() {
        return CreateCalendarRequest.builder()
                .title("title")
                .memo("memo")
                .staredAt("2021-08-01")
                .endedAt("2021-08-01")
                .startTime("10:00")
                .endTime("11:00")
                .isAllDay(false)
                .address("address")
                .roadAddress("roadAddress")
                .placeName("placeName")
                .build();
    }

    public static UpdateCalendarRequest updateCalendarRequest() {
        return UpdateCalendarRequest.builder()
                .title("title")
                .memo("memo")
                .staredAt("2021-08-01")
                .endedAt("2021-08-01")
                .startTime("10:00")
                .endTime("11:00")
                .isAllDay(false)
                .address("address")
                .roadAddress("roadAddress")
                .placeName("placeName")
                .build();
    }

    public static CheckAttendRequest checkAttendRequest() {
        return CheckAttendRequest.builder()
                .attendStatus("YES")
                .build();
    }

    public static Calendar getMockCalendar() {
        return createCalendarRequest().toEntity(1L);
    }
}
