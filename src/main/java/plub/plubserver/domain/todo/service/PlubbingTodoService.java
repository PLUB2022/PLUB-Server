package plub.plubserver.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.todo.dto.PlubbingTodoDto;
import plub.plubserver.domain.todo.repository.PlubbingTodoRepository;

import java.util.List;

import static plub.plubserver.domain.todo.dto.PlubbingTodoDto.*;

@Service
@RequiredArgsConstructor
public class PlubbingTodoService {

    private final PlubbingTodoRepository plubbingTodoRepository;
    private final AccountService accountService;


    public TodoIdResponse createTodoCard(CreateTodoRequest request) {
       return new PlubbingTodoDto.TodoIdResponse(1L);
    }

    public TodoListResponse getAllTodoList(Long plubbingId, Pageable pageable) {
        return getTodoListMockResponse(pageable);
    }


    public TodoCardResponse getTodoCard(Long plubbingId, Long todolistId) {
        return new TodoCardResponse(1L, "content1", "2022-12-01", false, false, "", 0);
    }

    public TodoListResponse getTodoList(Long plubbingId, Long accountId, Pageable pageable) {
        Account loginAccount = accountService.getCurrentAccount();
        return getTodoListMockResponse(pageable);
    }

    public TodoListResponse getTodoListByDate(Long plubbingId, int year, int month, int day, Pageable pageable) {
        return getTodoListMockResponse(pageable);
    }

    public TodoMessage deleteTodoList(Long plubbingId, Long todolistId) {
        return new TodoMessage("success");
    }

    public TodoCardResponse updateTodoList(Long plubbingId, Long todolistId, UpdateTodoRequest request) {
        return new TodoCardResponse(1L, "content1", "2022-12-01", false, false, "", 0);
    }

    public TodoMessage completeTodoList(Long plubbingId, Long todolistId) {
        return new TodoMessage("success");
    }

    public TodoMessage cancelTodoList(Long plubbingId, Long todolistId) {
        return new TodoMessage("success");
    }

    public TodoMessage proofTodoList(Long plubbingId, Long todolistId, ProofTodoRequest proofImage) {
        return new TodoMessage("success");
    }

    private TodoListResponse getTodoListMockResponse(Pageable pageable) {
        List<TodoCardResponse> todoCardResponses = List.of(
                new TodoCardResponse(1L, "content1", "2022-12-01", false, false, "", 0),
                new TodoCardResponse(2L, "content1", "2022-12-01", false, false, "", 0),
                new TodoCardResponse(3L, "content1", "2022-12-01", false, false, "", 0)
        );
        Page<TodoCardResponse> todoCardResponsePage = new PageImpl<>(todoCardResponses, pageable, 0);
        return TodoListResponse.of(todoCardResponsePage);
    }
}
