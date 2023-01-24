package plub.plubserver.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import plub.plubserver.domain.calendar.repository.PlubbingCalendarRepository;

import java.util.List;

import static plub.plubserver.domain.calendar.dto.PlubbingCalendarAttendDto.*;
import static plub.plubserver.domain.calendar.dto.PlubbingCalendarDto.*;

@Service
@RequiredArgsConstructor
public class PlubbingCalendarService {

    public final PlubbingCalendarRepository plubbingCalendarRepository;

    public CalendarCardResponse getCalendarCard(Long calendarId) {
        return new CalendarCardResponse(1L, "title", "memo", "2021-08-01", "2021-08-01", "10:00", "11:00", false, "address", "roadAddress", "placeName",
                new HostInfo(10L, "nickname", "profileImage"),
                new CalendarAttendList(List.of(new CalendarAttendResponse(100L, "nickname", "profileImage", "attendStatus"))));
    }

    public CalendarIdResponse createCalendar(Long plubbingId, CreateCalendarRequest createCalendarResponse) {
        return new CalendarIdResponse(1L);
    }

    public CalendarIdResponse updateCalendar(Long plubbingId, Long calendarId, UpdateCalendarRequest updateCalendarResponse) {
        return new CalendarIdResponse(1L);
    }

    public CalendarMessage deleteCalendar(Long plubbingId, Long calendarId) {
        return new CalendarMessage("캘린더 삭제 성공");
    }


    public CalendarAttendResponse checkAttend(Long plubbingId, Long calendarId, CheckAttendRequest calendarAttendRequest) {
        return new CalendarAttendResponse(1L, "nickname", "profileImage", "attendStatus");
    }

    public CalendarListResponse getCalendarList(Long plubbingId) {
        return new CalendarListResponse(
                List.of(
                        new CalendarCardResponse(1L, "title", "memo", "2021-08-01", "2021-08-01", "10:00", "11:00", false, "address", "roadAddress", "placeName",
                                new HostInfo(10L, "nickname", "profileImage"),
                                new CalendarAttendList(List.of(new CalendarAttendResponse(100L, "nickname", "profileImage", "attendStatus")))),
                        new CalendarCardResponse(2L, "title", "memo", "2021-08-01", "2021-08-01", "10:00", "11:00", false, "address", "roadAddress", "placeName",
                                new HostInfo(10L, "nickname", "profileImage"),
                                new CalendarAttendList(List.of(new CalendarAttendResponse(100L, "nickname", "profileImage", "attendStatus"))))));
    }

}
