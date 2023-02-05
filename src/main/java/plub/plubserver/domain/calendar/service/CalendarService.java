package plub.plubserver.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.calendar.config.CalendarCode;
import plub.plubserver.domain.calendar.exception.CalendarException;
import plub.plubserver.domain.calendar.model.AttendStatus;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.calendar.model.CalendarAttend;
import plub.plubserver.domain.calendar.repository.CalendarAttendRepository;
import plub.plubserver.domain.calendar.repository.CalendarRepository;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import static plub.plubserver.domain.calendar.dto.CalendarAttendDto.CalendarAttendResponse;
import static plub.plubserver.domain.calendar.dto.CalendarAttendDto.CheckAttendRequest;
import static plub.plubserver.domain.calendar.dto.CalendarDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    public final CalendarRepository calendarRepository;
    public final CalendarAttendRepository calendarAttendRepository;
    public final PlubbingService plubbingService;

    public CalendarCardResponse getCalendarCard(Long calendarId) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarException(CalendarCode.NOT_FOUNT_CALENDAR));
        return CalendarCardResponse.of(calendar);
    }

    @Transactional
    public CalendarIdResponse createCalendar(Account account, Long plubbingId, CreateCalendarRequest createCalendarResponse) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkHost(account, plubbing);
        Calendar calendar = createCalendarResponse.toEntity(account.getId());
        calendarRepository.save(calendar);
        return CalendarIdResponse.of(calendar.getId());
    }

    @Transactional
    public CalendarIdResponse updateCalendar(Account account, Long plubbingId, Long calendarId, UpdateCalendarRequest updateCalendarResponse) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkHost(account, plubbing);
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarException(CalendarCode.NOT_FOUNT_CALENDAR));
        calendar.updateCalendar(updateCalendarResponse);
        return CalendarIdResponse.of(calendar.getId());
    }

    public CalendarMessage softDeleteCalendar(Long plubbingId, Long calendarId) {
        plubbingService.getPlubbing(plubbingId);
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarException(CalendarCode.NOT_FOUNT_CALENDAR));
        calendar.softDelete();
        return new CalendarMessage("soft delete calendar");
    }

    @Transactional
    public CalendarAttendResponse checkAttend(Account account, Long plubbingId, Long calendarId, CheckAttendRequest calendarAttendRequest) {
        plubbingService.getPlubbing(plubbingId);
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarException(CalendarCode.NOT_FOUNT_CALENDAR));
        AttendStatus attendStatus = AttendStatus.valueOf(calendarAttendRequest.attendStatus());
        CalendarAttend calendarAttend = CalendarAttend.builder()
                .calendar(calendar)
                .account(account)
                .attendStatus(attendStatus)
                .build();
        calendarAttendRepository.save(calendarAttend);
        calendar.addCalendarAttend(calendarAttend);
        return CalendarAttendResponse.of(calendarAttend);
    }

    public CalendarListResponse getCalendarList(Long plubbingId, Pageable pageable) {
        plubbingService.getPlubbing(plubbingId);
        Page<CalendarCardResponse> calendarPage = calendarRepository.findAllByPlubbingId(plubbingId, pageable)
                .map(CalendarCardResponse::of);
        return CalendarListResponse.of(calendarPage);
    }
}
