package plub.plubserver.domain.calendar.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.calendar.model.Calendar;

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
        public CreateCalendarRequest {
        }

        public Calendar toEntity(Long hostId) {
            return Calendar.builder()
                    .title(title)
                    .memo(memo)
                    .staredAt(staredAt)
                    .endedAt(endedAt)
                    .startTime(startTime)
                    .endTime(endTime)
                    .isAllDay(isAllDay)
                    .address(address)
                    .roadAddress(roadAddress)
                    .placeName(placeName)
                    .hostId(hostId)
                    .build();
        }

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
        public UpdateCalendarRequest {
        }
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
            CalendarAttendList calendarAttendList
    ) {
        @Builder
        public CalendarCardResponse {
        }

        public static CalendarCardResponse of(Calendar calendar) {
            return CalendarCardResponse.builder()
                    .calendarId(calendar.getId())
                    .title(calendar.getTitle())
                    .memo(calendar.getMemo())
                    .staredAt(calendar.getStaredAt())
                    .endedAt(calendar.getEndedAt())
                    .startTime(calendar.getStartTime())
                    .endTime(calendar.getEndTime())
                    .isAllDay(calendar.isAllDay())
                    .address(calendar.getAddress())
                    .roadAddress(calendar.getRoadAddress())
                    .placeName(calendar.getPlaceName())
                    .calendarAttendList(CalendarAttendList.of(calendar.getCalendarAttendList()))
                    .build();
        }
    }


    public record CalendarListResponse(
            PageResponse<CalendarCardResponse> calendarList
    ) {
        @Builder
        public CalendarListResponse {
        }

        public static CalendarListResponse of(Page<CalendarCardResponse> calendarPage) {
            return CalendarListResponse.builder()
                    .calendarList(PageResponse.of(calendarPage))
                    .build();
        }
    }


    public record CalendarIdResponse(Long calendarId) {
        @Builder
        public CalendarIdResponse {
        }

        public static CalendarIdResponse of(Long calendarId) {
            return CalendarIdResponse.builder()
                    .calendarId(calendarId)
                    .build();
        }
    }

    public record CalendarMessage(Object result) {
    }

}
