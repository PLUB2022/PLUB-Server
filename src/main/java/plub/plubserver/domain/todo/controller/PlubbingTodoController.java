package plub.plubserver.domain.todo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.todo.service.PlubbingTodoService;

import javax.validation.Valid;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.todo.dto.PlubbingTodoDto.*;
import static plub.plubserver.domain.todo.dto.PlubbingTodoDto.CreateTodoRequest;
import static plub.plubserver.domain.todo.dto.PlubbingTodoDto.TodoCardResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plubbings")
@Slf4j
@Api(tags = "투두리스트 API")
public class PlubbingTodoController {

    public final PlubbingTodoService plubbingTodoService;

    @ApiOperation(value = "투두 리스트 생성")
    @PostMapping("/{plubbingId}/todolist")
    public ApiResponse<TodoIdResponse> createTodoCard(@PathVariable Long plubbingId,
                                                        @Valid @RequestBody CreateTodoRequest request) {
        return success(plubbingTodoService.createTodoCard(request));
    }

    @ApiOperation(value = "투두 리스트 전체 조회")
    @GetMapping("/{plubbingId}/todolist")
    public ApiResponse<TodoListResponse> getAllTodoList(@PathVariable Long plubbingId) {
        return success(plubbingTodoService.getAllTodoList(plubbingId));
    }

    @ApiOperation(value = "투두 상세 조회")
    @GetMapping("/{plubbingId}/todolist/{todolistId}")
    public ApiResponse<TodoCardResponse> getTodoCard(@PathVariable Long plubbingId,
                                                     @PathVariable Long todolistId) {
        return success(plubbingTodoService.getTodoCard(plubbingId, todolistId));
    }

    @ApiOperation(value = "투두 리스트 맴버 조회")
    @GetMapping("/{plubbingId}/todolist/account/{accountId}")
    public ApiResponse<TodoListResponse> getTodoList(@PathVariable Long plubbingId,
                                                     @PathVariable Long accountId) {
        return success(plubbingTodoService.getTodoList(plubbingId, accountId));
    }

    @ApiOperation(value = "투두 리스트 날짜 조회")
    @GetMapping("/{plubingId}/todolist?year={year}&month={month}&day={day}")
    public ApiResponse<TodoListResponse> getTodoListByDate(@PathVariable Long plubbingId,
                                                           @PathVariable int year,
                                                           @PathVariable int month,
                                                           @PathVariable int day) {
        return success(plubbingTodoService.getTodoListByDate(plubbingId, year, month, day));
    }

    @ApiOperation(value = "투두 리스트 삭제")
    @DeleteMapping("/{plubbingId}/todolist/{todolistId}")
    public ApiResponse<TodoMessage> deleteTodoList(@PathVariable Long plubbingId,
                                            @PathVariable Long todolistId) {
        return success(plubbingTodoService.deleteTodoList(plubbingId, todolistId));
    }

    @ApiOperation(value = "투두 리스트 수정")
    @PutMapping("/{plubbingId}/todolist/{todolistId}")
    public ApiResponse<TodoCardResponse> updateTodoList(@PathVariable Long plubbingId,
                                                        @PathVariable Long todolistId,
                                                        @Valid @RequestBody UpdateTodoRequest request) {
        return success(plubbingTodoService.updateTodoList(plubbingId, todolistId, request));
    }

    @ApiOperation(value = "투두 리스트 완료")
    @PutMapping("/{plubbingId}/todolist/{todoId}/complete")
    public ApiResponse<TodoMessage> completeTodoList(@PathVariable Long plubbingId,
                                                     @PathVariable Long todoId) {
        return success(plubbingTodoService.completeTodoList(plubbingId, todoId));
    }

    @ApiOperation(value = "투두 리스트 완료 취소")
    @PutMapping("/{plubbingId}/todolist/{todolistId}/cancel")
    public ApiResponse<TodoMessage> cancelTodoList(@PathVariable Long plubbingId,
                                                   @PathVariable Long todolistId) {
        return success(plubbingTodoService.cancelTodoList(plubbingId, todolistId));
    }

    @ApiOperation(value = "투두 리스트 인증")
    @PostMapping("/{plubbingId}/todolist/{todolistId}/proof")
    public ApiResponse<TodoMessage> proofTodoList(@PathVariable Long plubbingId,
                                                  @PathVariable Long todolistId,
                                                  @Valid @RequestBody ProofTodoRequest request) {
        return success(plubbingTodoService.proofTodoList(plubbingId, todolistId, request));
    }

}
