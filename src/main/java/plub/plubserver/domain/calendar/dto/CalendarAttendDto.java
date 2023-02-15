package plub.plubserver.domain.calendar.dto;

import lombok.Builder;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.calendar.model.AttendStatus;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.calendar.model.CalendarAttend;

import java.util.List;

public class CalendarAttendDto {

    public record CalendarAttendResponse(
            Long calendarAttendId,
            String nickname,
            String profileImage,
            String AttendStatus
    ) {
        @Builder
        public CalendarAttendResponse {
        }

        public static CalendarAttendResponse of(CalendarAttend calendarAttend) {
            return CalendarAttendResponse.builder()
                    .calendarAttendId(calendarAttend.getId())
                    .nickname(calendarAttend.getAccount().getNickname())
                    .profileImage(calendarAttend.getAccount().getProfileImage())
                    .AttendStatus(calendarAttend.getAttendStatus().toString())
                    .build();
        }
    }

    public record CalendarAttendList(
            List<CalendarAttendResponse> calendarAttendList
    ) {
        @Builder
        public CalendarAttendList {
        }

        public static CalendarAttendList of(List<CalendarAttend> calendarAttendList) {
            return CalendarAttendList.builder()
                    .calendarAttendList(calendarAttendList.stream()
                            .map(CalendarAttendResponse::of)
                            .toList())
                    .build();
        }
    }

    public record CheckAttendRequest(
            String attendStatus
    ) {
        @Builder
        public CheckAttendRequest {
        }

        public CalendarAttend toEntity(Calendar mockCalendar, Account account) {
            return CalendarAttend.builder()
                    .calendar(mockCalendar)
                    .account(account)
                    .attendStatus(AttendStatus.valueOf(attendStatus))
                    .build();
        }
    }

}
