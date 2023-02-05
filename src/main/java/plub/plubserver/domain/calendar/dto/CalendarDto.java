package plub.plubserver.domain.calendar.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
import plub.plubserver.common.dto.PageResponse;

import static plub.plubserver.domain.calendar.dto.CalendarAttendDto.CalendarAttendList;

public class CalendarDto {

    public record CreateCalendarRequest(
        String title,
        String memo,
        String staredAt,
        String endedAt,
        String startTime,
        String endTime,
        boolean isAllDay,
        String address,
        String roadAddress,
        String placeName
    ) {
        @Builder
        public CreateCalendarRequest {}

    }

    public record UpdateCalendarRequest(
        String title,
        String memo,
        String staredAt,
        String endedAt,
        String startTime,
        String endTime,
        boolean isAllDay,
        String address,
        String roadAddress,
        String placeName
    ) {
        @Builder
        public UpdateCalendarRequest {}
    }

    public record CalendarCardResponse(
        Long calendarId,
        String title,
        String memo,
        String staredAt,
        String endedAt,
        String startTime,
        String endTime,
        boolean isAllDay,
        String address,
        String roadAddress,
        String placeName,
        HostInfo hostInfo,
        CalendarAttendList calendarAttendList
    ) {
        @Builder
        public CalendarCardResponse{}
    }


    public record CalendarListResponse(
            PageResponse<CalendarCardResponse> calendarList
    ){
        @Builder
        public CalendarListResponse{}

        public static CalendarListResponse of(Page<CalendarCardResponse> calendarPage) {
            return CalendarListResponse.builder()
                    .calendarList(PageResponse.of(calendarPage))
                    .build();
        }
    }

    public record HostInfo(
        Long hostId,
        String hostName,
        String profileImage
    ) {
        @Builder
        public HostInfo{}
    }

    public record CalendarIdResponse(Long feedId) {
    }

    public record CalendarMessage(Object result) {
    }

}
