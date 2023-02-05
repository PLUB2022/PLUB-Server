package plub.plubserver.domain.calendar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plub.plubserver.domain.account.AccountTemplate;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.calendar.dto.CalendarAttendDto;
import plub.plubserver.domain.calendar.model.Calendar;
import plub.plubserver.domain.calendar.repository.CalendarAttendRepository;
import plub.plubserver.domain.calendar.repository.CalendarRepository;
import plub.plubserver.domain.calendar.service.CalendarService;
import plub.plubserver.domain.plubbing.PlubbingMockUtils;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static plub.plubserver.domain.calendar.dto.CalendarDto.CreateCalendarRequest;
import static plub.plubserver.domain.calendar.dto.CalendarDto.UpdateCalendarRequest;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @InjectMocks
    CalendarService calendarService;

    @Mock
    CalendarRepository calendarRepository;

    @Mock
    CalendarAttendRepository calendarAttendRepository;

    @Mock
    PlubbingService plubbingService;

    @Test
    @DisplayName("캘린더 생성 성공")
    void createCalendar_success() {
        // given
        Account account = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(account);
        given(plubbingService.getPlubbing(any()))
                .willReturn(plubbing);

        CreateCalendarRequest calendarRequest = CalendarMockUtils.createCalendarRequest();
        calendarRequest.toEntity(1L);

        // when
        calendarService.createCalendar(account, plubbing.getId(), calendarRequest);

        // then
        assertThat(calendarRequest.title()).isEqualTo(calendarRequest.title());
        assertThat(calendarRequest.memo()).isEqualTo(calendarRequest.memo());
        assertThat(calendarRequest.staredAt()).isEqualTo(calendarRequest.staredAt());
        assertThat(calendarRequest.endedAt()).isEqualTo(calendarRequest.endedAt());
        assertThat(calendarRequest.startTime()).isEqualTo(calendarRequest.startTime());
        assertThat(calendarRequest.endTime()).isEqualTo(calendarRequest.endTime());
        assertThat(calendarRequest.isAllDay()).isEqualTo(calendarRequest.isAllDay());
        assertThat(calendarRequest.address()).isEqualTo(calendarRequest.address());
        assertThat(calendarRequest.roadAddress()).isEqualTo(calendarRequest.roadAddress());
        assertThat(calendarRequest.placeName()).isEqualTo(calendarRequest.placeName());
    }

    @Test
    @DisplayName("캘린더 수정")
    void updateCalendar_success() {
        // given
        Account account = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(account);
        given(plubbingService.getPlubbing(any()))
                .willReturn(plubbing);

        Calendar mockCalendar = CalendarMockUtils.getMockCalendar();
        given(calendarRepository.findById(any()))
                .willReturn(java.util.Optional.of(mockCalendar));

        UpdateCalendarRequest calendarRequest = CalendarMockUtils.updateCalendarRequest();

        // when
        calendarService.updateCalendar(account, plubbing.getId(), mockCalendar.getId(), calendarRequest);

        // then
        assertThat(calendarRequest.title()).isEqualTo(calendarRequest.title());
        assertThat(calendarRequest.memo()).isEqualTo(calendarRequest.memo());
        assertThat(calendarRequest.staredAt()).isEqualTo(calendarRequest.staredAt());
        assertThat(calendarRequest.endedAt()).isEqualTo(calendarRequest.endedAt());
        assertThat(calendarRequest.startTime()).isEqualTo(calendarRequest.startTime());
        assertThat(calendarRequest.endTime()).isEqualTo(calendarRequest.endTime());
        assertThat(calendarRequest.isAllDay()).isEqualTo(calendarRequest.isAllDay());
        assertThat(calendarRequest.address()).isEqualTo(calendarRequest.address());
        assertThat(calendarRequest.roadAddress()).isEqualTo(calendarRequest.roadAddress());
        assertThat(calendarRequest.placeName()).isEqualTo(calendarRequest.placeName());
    }

    @Test
    @DisplayName("캘린더 checkAttend")
    void checkAttend_success() {
        // given
        Account account = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(account);
        given(plubbingService.getPlubbing(any()))
                .willReturn(plubbing);

        Calendar mockCalendar = CalendarMockUtils.getMockCalendar();
        given(calendarRepository.findById(any()))
                .willReturn(java.util.Optional.of(mockCalendar));

        CalendarAttendDto.CheckAttendRequest checkAttendRequest = CalendarMockUtils.checkAttendRequest();
        // when
        calendarService.checkAttend(account, plubbing.getId(), mockCalendar.getId(), checkAttendRequest);

        // then
        assertThat(mockCalendar.getCalendarAttendList().size()).isEqualTo(1);
    }
}