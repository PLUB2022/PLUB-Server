package plub.plubserver.domain.calendar.dto;

import lombok.Builder;
import org.springframework.data.domain.Page;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.calendar.model.CalendarAlarmType;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static plub.plubserver.domain.calendar.dto.CalendarAttendDto.CalendarAttendList;

public class CalendarDto {

    public record CreateCalendarRequest(
            @NotBlank @Size(max = 20)
            String title,
            @NotBlank @Size(max = 30)
            String memo,
            @NotBlank
            String startedAt,
            @NotBlank
            String endedAt,
            String startTime,
            String endTime,
            @NotNull
            boolean isAllDay,
            String address,
            String roadAddress,
            String placeName,
            String alarmType
    ) {
        @Builder
        public CreateCalendarRequest {
        }

        public Calendar toEntity(Account account, Plubbing plubbing, CalendarAlarmType calendarAlarmType) {
            return Calendar.builder()
                    .title(title)
                    .memo(memo)
                    .account(account)
                    .startedAt(startedAt)
                    .endedAt(endedAt)
                    .startTime(startTime)
                    .endTime(endTime)
                    .isAllDay(isAllDay)
                    .address(address)
                    .roadAddress(roadAddress)
                    .placeName(placeName)
                    .plubbing(plubbing)
                    .alarmType(calendarAlarmType)
                    .build();
        }

    }

    public record UpdateCalendarRequest(
            @NotBlank @Size(max = 20)
            String title,
            @NotBlank @Size(max = 30)
            String memo,
            @NotBlank
            String startedAt,
            @NotBlank
            String endedAt,
            String startTime,
            String endTime,
            @NotNull
            boolean isAllDay,
            String address,
            String roadAddress,
            String placeName,
            String alarmType
    ) {
        @Builder
        public UpdateCalendarRequest {
        }
    }

    public record CalendarCardResponse(
            Long calendarId,
            String title,
            String memo,
            String startedAt,
            String endedAt,
            String startTime,
            String endTime,
            boolean isAllDay,
            String address,
            String roadAddress,
            String placeName,
            boolean isAuthor,
            boolean isEditable,
            String alarmType,

            CalendarAttendList calendarAttendList
    ) {
        @Builder
        public CalendarCardResponse {
        }

        public static CalendarCardResponse of(
                Calendar calendar,
                boolean isAuthor,
                boolean isEditable,
                CalendarAttendList calendarAttendList
        ) {
            return CalendarCardResponse.builder()
                    .calendarId(calendar.getId())
                    .title(calendar.getTitle())
                    .memo(calendar.getMemo())
                    .startedAt(calendar.getStartedAt())
                    .endedAt(calendar.getEndedAt())
                    .startTime(calendar.getStartTime())
                    .endTime(calendar.getEndTime())
                    .isAllDay(calendar.isAllDay())
                    .address(calendar.getAddress())
                    .roadAddress(calendar.getRoadAddress())
                    .placeName(calendar.getPlaceName())
                    .isAuthor(isAuthor)
                    .isEditable(isEditable)
                    .alarmType(calendar.getAlarmType().toString())
                    .calendarAttendList(calendarAttendList)
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

        public static CalendarListResponse ofCursor(PageResponse<CalendarCardResponse> calendarPage) {
            return CalendarListResponse.builder()
                    .calendarList(calendarPage)
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
