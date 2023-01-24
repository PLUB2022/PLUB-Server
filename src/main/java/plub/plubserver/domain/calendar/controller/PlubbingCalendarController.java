package plub.plubserver.domain.calendar.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.calendar.dto.PlubbingCalendarAttendDto;
import plub.plubserver.domain.calendar.service.PlubbingCalendarService;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.calendar.dto.PlubbingCalendarAttendDto.*;
import static plub.plubserver.domain.calendar.dto.PlubbingCalendarDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbings")
@Slf4j
@Api(tags = "일정 API")
public class PlubbingCalendarController {

    private final PlubbingCalendarService plubbingCalendarService;

    @ApiOperation(value = "일정 생성")
    @PostMapping("/{plubbingId}/calendar")
    public ApiResponse<CalendarIdResponse> createCalendar(@PathVariable Long plubbingId,
                                                         @Valid @RequestBody CreateCalendarRequest createCalendarRequest) {
        return success(plubbingCalendarService.createCalendar(plubbingId, createCalendarRequest));
    }

    @ApiOperation(value = "일정 수정")
    @PutMapping("/{plubbingId}/calendar/{calendarId}")
    public ApiResponse<CalendarIdResponse> updateCalendar(@PathVariable Long plubbingId,
                                                         @PathVariable Long calendarId,
                                                         @Valid @RequestBody UpdateCalendarRequest updateCalendarRequest) {
        return success(plubbingCalendarService.updateCalendar(plubbingId, calendarId, updateCalendarRequest));
    }

    @ApiOperation(value = "일정 삭제")
    @DeleteMapping("/{plubbingId}/calendar/{calendarId}")
    public ApiResponse<CalendarMessage> deleteCalendar(@PathVariable Long plubbingId,
                                                         @PathVariable Long calendarId) {
        return success(plubbingCalendarService.deleteCalendar(plubbingId, calendarId));
    }

    @ApiOperation(value = "일정 상세 조회")
    @GetMapping("/{plubbingId}/calendar/{calendarId}")
    public ApiResponse<CalendarCardResponse> getCalendarCard(@PathVariable Long calendarId) {
        return success(plubbingCalendarService.getCalendarCard(calendarId));
    }

    @ApiOperation(value = "일정 리스트 조회")
    @GetMapping("/{plubbingId}/calendar")
    public ApiResponse<CalendarListResponse> getCalendarList(@PathVariable Long plubbingId) {
        return success(plubbingCalendarService.getCalendarList(plubbingId));
    }

    @ApiOperation(value = "참석 여부 선택")
    @PutMapping("/{plubbingId}/calendar/{calendarId}/attend")
    public ApiResponse<CalendarAttendResponse> attendCalendar(@PathVariable Long plubbingId,
                                                          @PathVariable Long calendarId,
                                                          @Valid @RequestBody PlubbingCalendarAttendDto.CheckAttendRequest calendarAttendRequest) {
        return success(plubbingCalendarService.checkAttend(plubbingId, calendarId, calendarAttendRequest));
    }


}
