package plub.plubserver.domain.todo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.todo.service.TodoService;

import javax.validation.Valid;

import java.time.LocalDate;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.todo.dto.TodoDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbings")
@Slf4j
@Api(tags = "투두리스트 API")
public class TodoController {

    public final TodoService todoService;
    public final AccountService accountService;

    @ApiOperation(value = "투두 리스트 생성")
    @PostMapping("/{plubbingId}/todolist")
    public ApiResponse<TodoIdResponse> createTodoCard(
            @PathVariable Long plubbingId,
            @Valid @RequestBody CreateTodoRequest request
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.createTodo(currentAccount, plubbingId, request));
    }

    @ApiOperation(value = "투두 상세 조회")
    @GetMapping("/{plubbingId}/todolist/{todoId}")
    public ApiResponse<TodoResponse> getTodoCard(
            @PathVariable Long plubbingId,
            @PathVariable Long todoId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.getTodo(currentAccount, plubbingId, todoId));
    }

    @ApiOperation(value = "투두 리스트 상세 조회")
    @GetMapping("/{plubbingId}/timeline/{timelineId}/todolist")
    public ApiResponse<TodoListResponse> getTodoTimeline(
            @PathVariable Long plubbingId,
            @PathVariable Long timelineId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.getTodoTimelineList(currentAccount, plubbingId, timelineId));
    }

    @ApiOperation(value = "투두 타임라인 전체 조회")
    @GetMapping("/{plubbingId}/timeline")
    public ApiResponse<PageResponse<TodoTimelineAllResponse>> getAllTodoList(
            @PathVariable Long plubbingId,
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) Long cursorId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.getAllTodoList(currentAccount, plubbingId, pageable, cursorId));
    }

    @ApiOperation(value = "투두 타임라인 날짜 조회 (캘린더 용)")
    @GetMapping("/{plubbingId}/timeline/{todoDate}")
    public ApiResponse<TodoTimelineResponse> getTodoTimeline(
            @PathVariable Long plubbingId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate todoDate
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.getTodoTimeline(currentAccount, plubbingId, todoDate));
    }

    @ApiOperation(value = "특정 회원 투두 타임라인 조회")
    @GetMapping("/{plubbingId}/timeline/accounts/{accountId}")
    public ApiResponse<PageResponse<TodoTimelineResponse>> getTodoListTest(
            @PathVariable Long plubbingId,
            @PathVariable Long accountId,
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) Long cursorId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.getAccountTodoTimelinePage(currentAccount, plubbingId, accountId, pageable, cursorId));
    }

    @ApiOperation(value = "투두 리스트 삭제 (캘린더 용)")
    @DeleteMapping("/{plubbingId}/todolist/{todoId}")
    public ApiResponse<TodoMessage> deleteTodoList(
            @PathVariable Long plubbingId,
            @PathVariable Long todoId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.deleteTodoList(currentAccount, plubbingId, todoId));
    }

    @ApiOperation(value = "투두 리스트 수정 (캘린더 용)")
    @PutMapping("/{plubbingId}/todolist/{todoId}")
    public ApiResponse<TodoResponse> updateTodoList(
            @PathVariable Long plubbingId,
            @PathVariable Long todoId,
            @Valid @RequestBody UpdateTodoRequest request
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.updateTodo(currentAccount, plubbingId, todoId, request));
    }

    @ApiOperation(value = "투두 리스트 완료 (캘린더 용)")
    @PutMapping("/{plubbingId}/todolist/{todoId}/complete")
    public ApiResponse<TodoResponse> completeTodoList(
            @PathVariable Long plubbingId,
            @PathVariable Long todoId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.completeTodo(currentAccount, plubbingId, todoId));
    }

    @ApiOperation(value = "투두 리스트 완료 취소 (캘린더 용)")
    @PutMapping("/{plubbingId}/todolist/{todoId}/cancel")
    public ApiResponse<TodoResponse> cancelTodoList(
            @PathVariable Long plubbingId,
            @PathVariable Long todoId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.cancelTodo(currentAccount, plubbingId, todoId));
    }

    @ApiOperation(value = "투두 리스트 인증 (캘린더 용)")
    @PostMapping("/{plubbingId}/todolist/{todoId}/proof")
    public ApiResponse<TodoResponse> proofTodoList(
            @PathVariable Long plubbingId,
            @PathVariable Long todoId,
            @Valid @RequestBody ProofTodoRequest request
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.proofTodo(currentAccount, plubbingId, todoId, request));
    }

    @ApiOperation(value = "투두 월 달력 조회 (캘린더 용)")
    @GetMapping("/{plubbingId}/timeline/year/{year}/month/{month}")
    public ApiResponse<TodoTimelineDateResponse> getTodoCalendarDateList(
            @PathVariable Long plubbingId,
            @PathVariable Integer year,
            @PathVariable Integer month
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.getTodoCalendarDateList(currentAccount, plubbingId, year, month));
    }

    @ApiOperation(value = "마이페이지 - 내 투두 조회")
    @GetMapping("/{plubbingId}/timeline/my")
    public ApiResponse<PageResponse<TodoTimelineResponse>> getMyTodoList(
            @PathVariable Long plubbingId,
            @PageableDefault Pageable pageable,
            @RequestParam(required = false) Long cursorId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.getMyTodoTimelinePage(currentAccount, plubbingId, pageable, cursorId));
    }

    @ApiOperation(value = "투두 좋아요")
    @PutMapping("/{plubbingId}/timeline/{timelineId}/like")
    public ApiResponse<TodoTimelineResponse> likeTodoTimeline(
            @PathVariable Long plubbingId,
            @PathVariable Long timelineId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.likeTodo(currentAccount, plubbingId, timelineId));
    }
}