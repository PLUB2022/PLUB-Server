package plub.plubserver.domain.calendar.dto;

import lombok.Builder;

import java.util.List;

import static plub.plubserver.domain.calendar.dto.PlubbingCalendarAttendDto.*;

public class PlubbingCalendarDto {

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
            List<CalendarCardResponse> calendarList
    ){
        @Builder
        public CalendarListResponse{}

        public static CalendarListResponse of(List<CalendarCardResponse> calendarList) {
            return CalendarListResponse.builder()
                    .calendarList(calendarList)
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
