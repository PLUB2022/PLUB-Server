package plub.plubserver.domain.calendar.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.calendar.service.CalendarService;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.calendar.dto.CalendarAttendDto.*;
import static plub.plubserver.domain.calendar.dto.CalendarDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbings")
@Slf4j
@Api(tags = "일정 API")
public class CalendarController {

    private final CalendarService calendarService;
    private final AccountService accountService;

    @ApiOperation(value = "일정 생성")
    @PostMapping("/{plubbingId}/calendar")
    public ApiResponse<CalendarIdResponse> createCalendar(
            @PathVariable Long plubbingId,
            @Valid @RequestBody CreateCalendarRequest createCalendarRequest
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(calendarService.createCalendar(currentAccount, plubbingId, createCalendarRequest));
    }

    @ApiOperation(value = "일정 수정")
    @PutMapping("/{plubbingId}/calendar/{calendarId}")
    public ApiResponse<CalendarIdResponse> updateCalendar(
            @PathVariable Long plubbingId,
            @PathVariable Long calendarId,
            @Valid @RequestBody UpdateCalendarRequest updateCalendarRequest
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(calendarService.updateCalendar(currentAccount, plubbingId, calendarId, updateCalendarRequest));
    }

    @ApiOperation(value = "일정 삭제")
    @DeleteMapping("/{plubbingId}/calendar/{calendarId}")
    public ApiResponse<CalendarMessage> deleteCalendar(
            @PathVariable Long plubbingId,
            @PathVariable Long calendarId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(calendarService.softDeleteCalendar(currentAccount, plubbingId, calendarId));
    }

    @ApiOperation(value = "일정 상세 조회")
    @GetMapping("/{plubbingId}/calendar/{calendarId}")
    public ApiResponse<CalendarCardResponse> getCalendarCard(
            @PathVariable Long plubbingId,
            @PathVariable Long calendarId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(calendarService.getCalendarCard(currentAccount, plubbingId, calendarId));
    }

    @ApiOperation(value = "일정 리스트 조회")
    @GetMapping("/{plubbingId}/calendar")
    public ApiResponse<CalendarListResponse> getCalendarList(
            @PathVariable Long plubbingId,
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) Long cursorId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(calendarService.getCalendarList(currentAccount, plubbingId, pageable, cursorId));
    }

    @ApiOperation(value = "참석 여부 선택")
    @PutMapping("/{plubbingId}/calendar/{calendarId}/attend")
    public ApiResponse<CalendarAttendResponse> attendCalendar(
            @PathVariable Long plubbingId,
            @PathVariable Long calendarId,
            @Valid @RequestBody CheckAttendRequest calendarAttendRequest
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(calendarService.checkAttend(currentAccount, plubbingId, calendarId, calendarAttendRequest));
    }

    @ApiOperation(value = "참석자 리스트 조회")
    @GetMapping("/{plubbingId}/calendar/{calendarId}/attend")
    public ApiResponse<CalendarAttendList> getAttendList(
            @PathVariable Long plubbingId,
            @PathVariable Long calendarId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(calendarService.getAttendList(currentAccount, plubbingId, calendarId));
    }
}
