package plub.plubserver.domain.todo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import plub.plubserver.domain.account.AccountTemplate;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.PlubbingMockUtils;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.domain.todo.exception.TodoException;
import plub.plubserver.domain.todo.model.Todo;
import plub.plubserver.domain.todo.repository.TodoRepository;
import plub.plubserver.domain.todo.repository.TodoTimelineRepository;
import plub.plubserver.domain.todo.service.TodoService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static plub.plubserver.domain.todo.dto.TodoDto.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @InjectMocks
    TodoService todoService;

    @Mock
    TodoRepository todoRepository;

    @Mock
    TodoTimelineRepository todoTimelineRepository;

    @Mock
    PlubbingService plubbingService;

    @Test
    @DisplayName("투두 생성 성공")
    void createTodo_success() {
        // given
        Account account = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(account);
        given(plubbingService.getPlubbing(any()))
                .willReturn(plubbing);

        CreateTodoRequest form = TodoMockUtils.createTodoRequest();
        Todo todo = form.toEntity(account);

        // when
        todoService.createTodo(account, plubbing.getId(), form);

        // then
        assertThat(todo.getDate()).isEqualTo(form.date());
        assertThat(todo.getContent()).isEqualTo(form.content());
        assertThat(todo.isChecked()).isFalse();
        assertThat(todo.isProof()).isFalse();
        assertThat(todo.getLikes()).isEqualTo(0);
        assertThat(todo.getAccount()).isEqualTo(account);
        assertThat(todo.getTodoTimeline()).isEqualTo(plubbing.getTodoTimelineList());
    }

    @Test
    @DisplayName("투두 수정 성공")
    void updateTodo_success() {
        // given
        Account account = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(account);
        given(plubbingService.getPlubbing(any()))
                .willReturn(plubbing);

        Todo todo = TodoMockUtils.getMockTodo(account);
        UpdateTodoRequest form = TodoMockUtils.updateTodoRequest();

        given(todoRepository.findByIdAndAccount(any(), any()))
                .willReturn(Optional.of(todo));

        // when
        todoService.updateTodo(account, plubbing.getId(), todo.getId(), form);

        // then
        assertThat(todo.getDate()).isEqualTo(form.date());
        assertThat(todo.getContent()).isEqualTo(form.content());
        assertThat(todo.isChecked()).isFalse();
        assertThat(todo.isProof()).isFalse();
        assertThat(todo.getLikes()).isEqualTo(0);
        assertThat(todo.getAccount()).isEqualTo(account);
        assertThat(todo.getTodoTimeline()).isEqualTo(plubbing.getTodoTimelineList());
    }

    @Test
    @DisplayName("투두 완료 성공")
    void complete_todo_success() {
        // given
        Account account = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(account);
        given(plubbingService.getPlubbing(any()))
                .willReturn(plubbing);

        Todo mockTodo = TodoMockUtils.getMockTodo(account);
        given(todoRepository.findByIdAndAccount(any(), any()))
                .willReturn(Optional.of(mockTodo));

        // when
        todoService.completeTodo(account, plubbing.getId(), mockTodo.getId());

        // then
        assertThat(mockTodo.isChecked()).isTrue();
    }

    @Test
    @DisplayName("투두 인증 성공")
    void proof_todo_success() {
        // given
        Account account = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(account);
        given(plubbingService.getPlubbing(any()))
                .willReturn(plubbing);

        Todo mockTodo = TodoMockUtils.getMockCompleteTodo(account);
        given(todoRepository.findByIdAndAccount(any(), any()))
                .willReturn(Optional.of(mockTodo));

        ProofTodoRequest proofTodoRequest = TodoMockUtils.proofTodoRequest();

        // when
        todoService.proofTodo(account, plubbing.getId(), mockTodo.getId(), proofTodoRequest);

        // then
        assertThat(mockTodo.isProof()).isTrue();
    }

    @Test
    @DisplayName("투두 인증 실패")
    void proof_todo_fail() {
        // given
        Account account = AccountTemplate.makeAccount1();
        Plubbing plubbing = PlubbingMockUtils.getMockPlubbing(account);
        given(plubbingService.getPlubbing(any()))
                .willReturn(plubbing);

        Todo mockTodo = TodoMockUtils.getMockTodo(account);
        given(todoRepository.findByIdAndAccount(any(), any()))
                .willReturn(Optional.of(mockTodo));

        ProofTodoRequest proofTodoRequest = TodoMockUtils.proofTodoRequest();

        // when- then
        assertThatThrownBy(()-> todoService.proofTodo(account, plubbing.getId(), mockTodo.getId(), proofTodoRequest))
                .isInstanceOf(TodoException.class)
                .hasMessage("not complete todo");
    }
}