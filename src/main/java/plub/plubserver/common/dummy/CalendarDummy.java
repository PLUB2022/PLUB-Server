package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.calendar.dto.CalendarAttendDto;
import plub.plubserver.domain.calendar.dto.CalendarDto;
import plub.plubserver.domain.calendar.repository.CalendarRepository;
import plub.plubserver.domain.calendar.service.CalendarService;

import javax.annotation.PostConstruct;

@Slf4j
@Component("calendarDummy")
@DependsOn("todoDummy")
@RequiredArgsConstructor
public class CalendarDummy {
    private final CalendarService calendarService;
    private final AccountService accountService;
    private final CalendarRepository calendarRepository;

    @PostConstruct
    public void init() {
        if (calendarRepository.count() > 0) {
            log.info("[6] 캘린더가 존재여 더미를 생성하지 않았습니다.");
            return;
        }

        Account admin1 = accountService.getAccountByEmail("admin1");
        Account admin2 = accountService.getAccountByEmail("admin2");

        // 캘린더 더미 (plubbingId = 1)
        for (int i = 1; i < 5; i++) {
            CalendarDto.CreateCalendarRequest form = CalendarDto.CreateCalendarRequest.builder()
                    .title("calendar title " + i)
                    .memo("calendar memo " + i)
                    .staredAt("2023-01-" + (i + 10))
                    .endedAt("2023-01-" + (i + 10))
                    .startTime("10:00")
                    .endTime("11:00")
                    .isAllDay(false)
                    .address("calendar address " + i)
                    .placeName("calendar placeName " + i)
                    .roadAddress("calendar roadAddress " + i)
                    .build();
            calendarService.createCalendar(admin1, 1L, form);
        }

        for (int i = 5; i < 9; i++) {
            CalendarDto.CreateCalendarRequest form = CalendarDto.CreateCalendarRequest.builder()
                    .title("calendar title " + i)
                    .memo("calendar memo " + i)
                    .staredAt("2023-01-" + (i + 10))
                    .endedAt("2023-01-" + (i + 10))
                    .isAllDay(true)
                    .address("calendar address " + i)
                    .placeName("calendar placeName " + i)
                    .roadAddress("calendar roadAddress " + i)
                    .build();
            calendarService.createCalendar(admin1, 1L, form);
        }


        // 캘린더 참여 더미 (plubbingId = 1)
        for (int i = 1; i < 5; i++) {
            CalendarAttendDto.CheckAttendRequest form = CalendarAttendDto.CheckAttendRequest.builder()
                    .attendStatus("YES")
                    .build();
            calendarService.checkAttend(admin1, 1L, (long) i, form);
            calendarService.checkAttend(admin2, 1L, (long) i, form);
        }

        for (int i = 5; i < 7; i++) {
            CalendarAttendDto.CheckAttendRequest form = CalendarAttendDto.CheckAttendRequest.builder()
                    .attendStatus("YES")
                    .build();
            calendarService.checkAttend(admin2, 1L, (long) i, form);
        }

        log.info("[6] 캘린더 더미 생성 완료.");
    }
}
