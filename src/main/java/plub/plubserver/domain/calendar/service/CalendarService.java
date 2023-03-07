package plub.plubserver.domain.calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.calendar.exception.CalendarException;
import plub.plubserver.domain.calendar.model.AttendStatus;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.calendar.model.CalendarAlarmType;
import plub.plubserver.domain.calendar.model.CalendarAttend;
import plub.plubserver.domain.calendar.repository.CalendarAttendRepository;
import plub.plubserver.domain.calendar.repository.CalendarRepository;
import plub.plubserver.domain.notification.service.NotificationService;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import java.util.List;
import java.util.stream.Collectors;

import static plub.plubserver.domain.calendar.dto.CalendarAttendDto.*;
import static plub.plubserver.domain.calendar.dto.CalendarDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    public final CalendarRepository calendarRepository;
    public final CalendarAttendRepository calendarAttendRepository;
    public final PlubbingService plubbingService;
    public final NotificationService notificationService;

    public CalendarCardResponse getCalendarCard(Long plubbingId, Long calendarId) {
        Calendar calendar = calendarRepository.findByIdAndPlubbingIdAndVisibilityIsTrue(calendarId, plubbingId)
                .orElseThrow(() -> new CalendarException(StatusCode.NOT_FOUNT_CALENDAR));
        List<CalendarAttend> calendarAttendList = calendar.getCalendarAttendList().stream()
                .filter(calendarAttend -> calendarAttend.getAttendStatus().equals(AttendStatus.YES))
                .collect(Collectors.toList());
        CalendarAttendList list = CalendarAttendList.of(calendarAttendList);
        return CalendarCardResponse.of(calendar, list);
    }

    public CreateCalendarRequest checkCalender(CreateCalendarRequest request) {
        String startTime = request.startTime();
        String endTime = request.endTime();
        String address = request.address();
        String roadAddress = request.roadAddress();
        String placeName = request.placeName();
        if (request.isAllDay()) {
            startTime = "00:00";
            endTime = "23:59";
        }
        if (request.address() == null || request.roadAddress() == null || request.placeName() == null) {
            address = " ";
            roadAddress = " ";
            placeName = " ";
        }
        return CreateCalendarRequest.builder()
                .title(request.title())
                .memo(request.memo())
                .startedAt(request.startedAt())
                .endedAt(request.endedAt())
                .startTime(startTime)
                .endTime(endTime)
                .isAllDay(request.isAllDay())
                .address(address)
                .roadAddress(roadAddress)
                .placeName(placeName)
                .build();
    }

    @Transactional
    public CalendarIdResponse createCalendar(Account account, Long plubbingId, CreateCalendarRequest request) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkHost(account, plubbing);
        CreateCalendarRequest createCalendarRequest = checkCalender(request);
        CalendarAlarmType calendarAlarmType = CalendarAlarmType.valueOf(request.alarmType());
        Calendar calendar = createCalendarRequest.toEntity(account.getId(), plubbing, calendarAlarmType);
        calendarRepository.save(calendar);
        List<AccountPlubbing> accountPlubbingList = plubbing.getAccountPlubbingList();
        for (AccountPlubbing accountPlubbing : accountPlubbingList) {
            CalendarAttend calendarAttend = CalendarAttend.builder()
                    .calendar(calendar)
                    .account(accountPlubbing.getAccount())
                    .attendStatus(AttendStatus.WAITING)
                    .build();
            calendarAttendRepository.save(calendarAttend);
        }

        // 멤버들에게 푸시 알림
        plubbing.getMembers().forEach(member -> {
            notificationService.pushMessage(
                    member,
                    plubbing.getName(),
                    "새로운 일정이 등록되었어요! 모이는 시간과 장소를 확인하고 참여해 보세요!\n : " + calendar.getTitle() + "," + calendar.getStartedAt() + " ~ " + calendar.getEndedAt() + "," + calendar.getPlaceName()
            );
        });

        //TODO: 푸시 알림 스케줄러에 등록

        return CalendarIdResponse.of(calendar.getId());
    }

    @Transactional
    public CalendarIdResponse updateCalendar(Account account, Long plubbingId, Long calendarId, UpdateCalendarRequest updateCalendarResponse) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkHost(account, plubbing);
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarException(StatusCode.NOT_FOUNT_CALENDAR));
        calendar.updateCalendar(updateCalendarResponse);

        // 멤버들에게 푸시 알림
        plubbing.getMembers().forEach(member -> {
            notificationService.pushMessage(
                    member,
                    plubbing.getName(),
                    "모임 일정이 수정되었어요. 어떻게 변경되었는지 확인해 볼까요?\n : " + calendar.getTitle() + "," + calendar.getStartedAt() + " ~ " + calendar.getEndedAt() + "," + calendar.getPlaceName()
            );
        });

        //TODO: 푸시 알림 스케줄러에 변경

        return CalendarIdResponse.of(calendar.getId());
    }

    @Transactional
    public CalendarMessage softDeleteCalendar(Long plubbingId, Long calendarId) {
        plubbingService.getPlubbing(plubbingId);
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarException(StatusCode.NOT_FOUNT_CALENDAR));
        calendar.softDelete();
        return new CalendarMessage("soft delete calendar");
    }

    @Transactional
    public CalendarAttendResponse checkAttend(Account account, Long plubbingId, Long calendarId, CheckAttendRequest calendarAttendRequest) {
        plubbingService.getPlubbing(plubbingId);
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarException(StatusCode.NOT_FOUNT_CALENDAR));
        AttendStatus attendStatus = AttendStatus.valueOf(calendarAttendRequest.attendStatus());
        CalendarAttend calendarAttend = calendarAttendRepository.findByCalendarIdAndAccountId(calendar.getId(), account.getId())
                .orElseThrow(() -> new CalendarException(StatusCode.NOT_FOUNT_CALENDAR_ATTEND));
        calendarAttend.updateAttendStatus(attendStatus);
        return CalendarAttendResponse.of(calendarAttend);
    }

    public CalendarListResponse getCalendarList(Long plubbingId, Pageable pageable, Long cursorId) {
        plubbingService.getPlubbing(plubbingId);
        Page<CalendarCardResponse> calendarPage = calendarRepository.findAllByPlubbingId(plubbingId, pageable, cursorId)
                .map(calendar -> {
                    List<CalendarAttend> calendarAttendList = calendar.getCalendarAttendList().stream()
                            .filter(calendarAttend -> calendarAttend.getAttendStatus().equals(AttendStatus.YES))
                            .toList();
                    CalendarAttendList list = CalendarAttendList.of(calendarAttendList);
                    return CalendarCardResponse.of(calendar, list);
                });
        Long totalElements = calendarRepository.countAllByPlubbing(plubbingId);
        PageResponse<CalendarCardResponse> response = PageResponse.ofCursor(calendarPage, totalElements);
        return CalendarListResponse.ofCursor(response);
    }

    public CalendarAttendList getAttendList(Long plubbingId, Long calendarId) {
        plubbingService.getPlubbing(plubbingId);
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new CalendarException(StatusCode.NOT_FOUNT_CALENDAR));
        List<CalendarAttend> attendList = calendarAttendRepository.findByCalendarIdOrderByAttendStatus(calendar.getId())
                .stream().filter(calendarAttend -> calendarAttend.getAttendStatus().equals(AttendStatus.YES))
                .toList();
        return CalendarAttendList.of(attendList);

    }
}
