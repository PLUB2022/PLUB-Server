package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.calendar.dto.CalendarAttendDto;
import plub.plubserver.domain.calendar.dto.CalendarDto;
import plub.plubserver.domain.calendar.repository.CalendarAttendRepository;
import plub.plubserver.domain.calendar.repository.CalendarRepository;
import plub.plubserver.domain.calendar.service.CalendarService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component("calendarDummy")
@DependsOn("todoDummy")
@RequiredArgsConstructor
public class CalendarDummy {
    private final CalendarService calendarService;
    private final AccountService accountService;
    private final CalendarRepository calendarRepository;
    private final CalendarAttendRepository calendarAttendRepository;

    @PostConstruct
    @Transactional
    public void init() {
        if (calendarRepository.count() > 0) {
            log.info("[6] 캘린더가 존재하여 더미를 생성하지 않았습니다.");
            return;
        }

        Account admin1 = accountService.getAccountByEmail("admin1");

        // 캘린더 더미 (plubbingId = 1)
        for (int i = 1; i < 7; i++) {
            CalendarDto.CreateCalendarRequest form = CalendarDto.CreateCalendarRequest.builder()
                    .title("calendar title " + i)
                    .memo("calendar memo " + i)
                    .startedAt("2023-01-" + (i + 10))
                    .endedAt("2023-01-" + (i + 11))
                    .startTime("10:00")
                    .endTime("11:00")
                    .isAllDay(false)
                    .address("calendar address " + i)
                    .placeName("calendar placeName " + i)
                    .roadAddress("calendar roadAddress " + i)
                    .alarmType("FIVE_MINUTES")
                    .build();
            calendarService.createCalendar(admin1, 1L, form);
        }

        for (int i = 7; i < 11; i++) {
            CalendarDto.CreateCalendarRequest form = CalendarDto.CreateCalendarRequest.builder()
                    .title("calendar title " + i)
                    .memo("calendar memo " + i)
                    .startedAt("2022-11-" + (i + 10))
                    .endedAt("2022-11-" + (i + 11))
                    .isAllDay(true)
                    .address("calendar address " + i)
                    .placeName("calendar placeName " + i)
                    .roadAddress("calendar roadAddress " + i)
                    .alarmType("TEN_MINUTES")
                    .build();
            calendarService.createCalendar(admin1, 1L, form);
        }

        Long checkCalendarId = 1L;

        // 캘린더 참여 더미 (plubbingId = 1)
        List<Integer> list = new ArrayList<>(Arrays.asList(1, 3, 4, 6, 7, 10, 14, 20));
        for(int j : list) {
            for (int i = 1; i <= j; i++) {
                Account account = accountService.getAccount((long) i);
                CalendarAttendDto.CheckAttendRequest form = CalendarAttendDto.CheckAttendRequest.builder()
                        .attendStatus("YES")
                        .build();
                calendarService.checkAttend(account, 1L, checkCalendarId, form);
            }
            checkCalendarId++;

        }

        log.info("[6] 캘린더 더미 생성 완료.");
    }
}
