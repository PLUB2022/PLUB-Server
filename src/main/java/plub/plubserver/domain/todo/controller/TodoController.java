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
    public ApiResponse<TodoIdResponse> createTodoCard(@PathVariable Long plubbingId,
                                                      @Valid @RequestBody CreateTodoRequest request) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.createTodo(currentAccount, plubbingId, request));
    }

    @ApiOperation(value = "투두 상세 조회")
    @GetMapping("/{plubbingId}/todolist/{todoId}")
    public ApiResponse<TodoResponse> getTodoCard(@PathVariable Long plubbingId,
                                                 @PathVariable Long todoId) {
        return success(todoService.getTodo(plubbingId, todoId));
    }

    @ApiOperation(value = "투두 타임라인 전체 조회")
    @GetMapping("/{plubbingId}/timeline")
    public ApiResponse<TodoTimelineAllPageResponse> getAllTodoList(@PathVariable Long plubbingId,
                                                                   @PageableDefault Pageable pageable) {
        return success(todoService.getAllTodoList(plubbingId, pageable));
    }

    @ApiOperation(value = "투두 타임라인 날짜 조회")
    @GetMapping("/{plubbingId}/timeline/{date}")
    public ApiResponse<TodoTimelineListResponse> getTodoTimeline(@PathVariable Long plubbingId,
                                                                 @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return success(todoService.getTodoTimeline(plubbingId, date));
    }

    @ApiOperation(value = "특정 회원 투두 타임라인 조회")
    @GetMapping("/{plubbingId}/timeline/accounts/{accountId}")
    public ApiResponse<TodoTimelinePageResponse> getTodoListTest(@PathVariable Long plubbingId,
                                                                 @PathVariable Long accountId,
                                                                 @PageableDefault Pageable pageable) {
        return success(todoService.getAccountTodoTimelinePage(plubbingId, accountId, pageable));
    }

    @ApiOperation(value = "투두 리스트 삭제")
    @DeleteMapping("/{plubbingId}/todolist/{todolistId}")
    public ApiResponse<TodoMessage> deleteTodoList(@PathVariable Long plubbingId,
                                                   @PathVariable Long todolistId) {
        return success(todoService.deleteTodoList(plubbingId, todolistId));
    }

    @ApiOperation(value = "투두 리스트 수정")
    @PutMapping("/{plubbingId}/todolist/{todoId}")
    public ApiResponse<TodoResponse> updateTodoList(@PathVariable Long plubbingId,
                                                    @PathVariable Long todoId,
                                                    @Valid @RequestBody UpdateTodoRequest request) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.updateTodo(currentAccount, plubbingId, todoId, request));
    }

    @ApiOperation(value = "투두 리스트 완료")
    @PutMapping("/{plubbingId}/todolist/{todoId}/complete")
    public ApiResponse<TodoIdResponse> completeTodoList(@PathVariable Long plubbingId,
                                                        @PathVariable Long todoId) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.completeTodo(currentAccount, plubbingId, todoId));
    }

    @ApiOperation(value = "투두 리스트 완료 취소")
    @PutMapping("/{plubbingId}/todolist/{todoId}/cancel")
    public ApiResponse<TodoIdResponse> cancelTodoList(@PathVariable Long plubbingId,
                                                      @PathVariable Long todoId) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.cancelTodo(currentAccount, plubbingId, todoId));
    }

    @ApiOperation(value = "투두 리스트 인증")
    @PostMapping("/{plubbingId}/todolist/{todoId}/proof")
    public ApiResponse<TodoResponse> proofTodoList(@PathVariable Long plubbingId,
                                                   @PathVariable Long todoId,
                                                   @Valid @RequestBody ProofTodoRequest request) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(todoService.proofTodo(currentAccount, plubbingId, todoId, request));
    }
}
