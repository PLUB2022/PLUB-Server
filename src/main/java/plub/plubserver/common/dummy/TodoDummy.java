package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;
import plub.plubserver.domain.todo.dto.TodoDto;
import plub.plubserver.domain.todo.repository.TodoRepository;
import plub.plubserver.domain.todo.service.TodoService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Slf4j
@Component("todoDummy")
@DependsOn("feedDummy")
@RequiredArgsConstructor
public class TodoDummy {
    private final TodoService todoService;
    private final AccountService accountService;
    private final TodoRepository todoRepository;

    @PostConstruct
    public void init() {
        if (todoRepository.count() > 0) {
            log.info("[5] 투두가 존재하여 더미를 생성하지 않았습니다.");
            return;
        }

        Account admin1 = accountService.getAccountByEmail("admin1");
        Account admin2 = accountService.getAccountByEmail("admin2");

        // 투두 더미 (plubbingId = 1)
        for (int i = 1; i < 11; i++) {
            TodoDto.CreateTodoRequest form = TodoDto.CreateTodoRequest.builder()
                    .date(LocalDate.parse("2023-01-" + (i + 10)))
                    .content("todo content " + i)
                    .build();

            todoService.createTodo(admin1, 1L, form);
            todoService.createTodo(admin1, 1L, form);
            todoService.createTodo(admin1, 1L, form);
        }

        for (int i = 11; i < 21; i++) {
            TodoDto.CreateTodoRequest form = TodoDto.CreateTodoRequest.builder()
                    .date(LocalDate.parse("2023-01-" + (i + 10)))
                    .content("todo content " + i)
                    .build();

            todoService.createTodo(admin1, 1L, form);
            todoService.createTodo(admin1, 1L, form);
        }

        for (int i = 1; i < 11; i++) {
            TodoDto.CreateTodoRequest form = TodoDto.CreateTodoRequest.builder()
                    .date(LocalDate.parse("2023-01-" + (i + 12)))
                    .content("todo content " + i)
                    .build();

            todoService.createTodo(admin2, 1L, form);
        }

        log.info("[5] 투두 더미 생성 완료.");
    }
}
