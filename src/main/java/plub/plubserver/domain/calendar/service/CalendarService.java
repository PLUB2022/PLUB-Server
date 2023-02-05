package plub.plubserver.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import plub.plubserver.domain.calendar.repository.CalendarRepository;

import java.util.List;

import static plub.plubserver.domain.calendar.dto.CalendarAttendDto.*;
import static plub.plubserver.domain.calendar.dto.CalendarDto.*;

@Service
@RequiredArgsConstructor
public class CalendarService {

    public final CalendarRepository calendarRepository;

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

    public CalendarListResponse getCalendarList(Long plubbingId, Pageable pageable) {

        List<CalendarCardResponse> calendarCardResponseList = List.of(
                new CalendarCardResponse(1L, "title", "memo", "2021-08-01", "2021-08-01", "10:00", "11:00", false, "address", "roadAddress", "placeName",
                        new HostInfo(10L, "nickname", "profileImage"),
                        new CalendarAttendList(List.of(new CalendarAttendResponse(100L, "nickname", "profileImage", "attendStatus")))),
                new CalendarCardResponse(2L, "title", "memo", "2021-08-01", "2021-08-01", "10:00", "11:00", false, "address", "roadAddress", "placeName",
                        new HostInfo(10L, "nickname", "profileImage"),
                        new CalendarAttendList(List.of(new CalendarAttendResponse(100L, "nickname", "profileImage", "attendStatus")))));
        Page<CalendarCardResponse> calendarCardResponsePage = new PageImpl<>(calendarCardResponseList, pageable, 0);
        return CalendarListResponse.of(calendarCardResponsePage);

    }

}
