package plub.plubserver.domain.todo;

import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.todo.model.Todo;

import java.time.LocalDate;

import static plub.plubserver.domain.todo.dto.TodoDto.*;

public class TodoMockUtils {
    public static CreateTodoRequest createTodoRequest() {
        return CreateTodoRequest.builder()
                .content("content")
                .date(LocalDate.now())
                .build();
    }

    public static UpdateTodoRequest updateTodoRequest() {
        return UpdateTodoRequest.builder()
                .content("content123")
                .date(LocalDate.now())
                .build();
    }

    public static ProofTodoRequest proofTodoRequest() {
        return ProofTodoRequest.builder()
                .proofImage("proofImage")
                .build();
    }

    public static Todo getMockTodo(Account account) {
        return createTodoRequest().toEntity(account);
    }

    public static Todo getMockCompleteTodo(Account account) {
        Todo todo = createTodoRequest().toEntity(account);
        todo.updateTodoIsChecked(true);
        return todo;
    }
}
