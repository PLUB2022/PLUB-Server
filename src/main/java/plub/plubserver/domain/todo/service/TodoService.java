package plub.plubserver.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import plub.plubserver.domain.account.config.AccountCode;
import plub.plubserver.domain.account.dto.AccountDto;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.domain.todo.config.TodoCode;
import plub.plubserver.domain.todo.exception.TodoException;
import plub.plubserver.domain.todo.model.Todo;
import plub.plubserver.domain.todo.model.TodoTimeline;
import plub.plubserver.domain.todo.repository.TodoRepository;
import plub.plubserver.domain.todo.repository.TodoTimelineRepository;

import java.time.LocalDate;
import java.util.List;

import static plub.plubserver.domain.todo.dto.TodoDto.*;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoTimelineRepository todoTimelineRepository;
    private final PlubbingService plubbingService;
    private final AccountRepository accountRepository;

    public TodoIdResponse createTodo(Account currentAccount, Long plubbingId, CreateTodoRequest request) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);

        // 투두 생성
        Todo todo = request.toEntity(currentAccount);
        todoRepository.save(todo);

        todoTimelineRepository.findByDateAndAccount(request.date(), currentAccount)
                .ifPresentOrElse(todoTimeline -> {
                    // 투두 타임라인 업데이트
                    todoTimeline.updateTodo(todo);
                    todoTimelineRepository.save(todoTimeline);
                    todoRepository.save(todo);
                    System.out.println("투두 타임라인 업데이트");
                }, () -> {
                    // 투두 타임라인 생성
                    TodoTimeline todoTimeline = TodoTimeline.builder()
                            .date(request.date())
                            .account(currentAccount)
                            .plubbing(plubbing)
                            .todoList(List.of(todo))
                            .build();
                    todo.updateTodoTimeline(todoTimeline);
                    todoTimelineRepository.save(todoTimeline);
                    todoRepository.save(todo);
                    System.out.println("투두 타임라인 생성");
                });

        return new TodoIdResponse(todo.getId());
    }

    // 투두 상세 조회
    public TodoResponse getTodo(Long plubbingId, Long todoId) {
        plubbingService.getPlubbing(plubbingId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoException(TodoCode.NOT_FOUNT_TODO));
        return TodoResponse.of(todo);
    }

    // 투두 타임라인 조회 (날짜)
    public TodoTimelineListResponse getTodoTimeline(Long plubbingId, LocalDate date) {
        plubbingService.getPlubbing(plubbingId);
        List<TodoTimeline> todoTimeline = todoTimelineRepository.findByDate(date);
        return TodoTimelineListResponse.of(todoTimeline);

    }

    // 투두 삭제
    public TodoMessage deleteTodoList(Long plubbingId, Long todoId) {
        plubbingService.getPlubbing(plubbingId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoException(TodoCode.NOT_FOUNT_TODO));
        todoRepository.delete(todo);
        return new TodoMessage("투두 삭제 성공");
    }

    // 투두 업데이트
    public TodoResponse updateTodo(Long plubbingId, Long todoId, UpdateTodoRequest request, Account currentAccount) {
        plubbingService.getPlubbing(plubbingId);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(TodoCode.NOT_FOUNT_TODO));
        if (todo.isChecked())
            throw new TodoException(TodoCode.ALREADY_CHECKED_TODO);

        todo.updateTodoDateAndContent(request.date(), request.content());
        todoRepository.save(todo);
        return TodoResponse.of(todo);
    }

    // 투두 완료
    public TodoIdResponse completeTodo(Long plubbingId, Long todoId, Account currentAccount) {
        plubbingService.getPlubbing(plubbingId);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(TodoCode.NOT_FOUNT_TODO));
        todo.updateTodoIsChecked(true);
        todoRepository.save(todo);
        return new TodoIdResponse(todo.getId());
    }

    // 투두 완료 취소
    public TodoIdResponse cancelTodo(Long plubbingId, Long todoId, Account currentAccount) {
        plubbingService.getPlubbing(plubbingId);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(TodoCode.NOT_FOUNT_TODO));
        if (todo.isProof())
            throw new TodoException(TodoCode.ALREADY_PROOF_TODO);
        todo.updateTodoIsChecked(false);
        todoRepository.save(todo);
        return new TodoIdResponse(todo.getId());
    }

    // 투두 인증
    public TodoResponse proofTodo(Long plubbingId, Long todoId, ProofTodoRequest proofImage, Account currentAccount) {
        plubbingService.getPlubbing(plubbingId);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(TodoCode.NOT_FOUNT_TODO));
        if (todo.isChecked()) {
            todo.updateTodoProofImage(proofImage.proofImage());
            todo.updateTodoIsProof(true);
            todoRepository.save(todo);
            return TodoResponse.of(todo);
        } else if (todo.isProof()){
            throw new TodoException(TodoCode.ALREADY_PROOF_TODO);
        } else {
            throw new TodoException(TodoCode.NOT_COMPLETE_TODO);
        }
    }

    // 회원 타임라인 조회
    public TodoTimelinePageResponse getAccountTodoTimelinePage(Long plubbingId, Long accountId, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));
        plubbingService.getPlubbing(plubbingId);
        Page<TodoTimelineResponse> todoTimelinePage = todoTimelineRepository.findByAccount(account, pageable)
                .map(TodoTimelineResponse::of);
        return TodoTimelinePageResponse.of(todoTimelinePage, AccountDto.AccountInfo.of(account));
    }

    // 타임라인 전체 조회
    public TodoTimelineAllPageResponse getAllTodoList(Long plubbingId, Pageable pageable) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        Page<TodoTimelineAllResponse> timelineResponsePage = todoTimelineRepository.findAllByPlubbing(plubbing, pageable)
                .map(TodoTimelineAllResponse::of);
        return TodoTimelineAllPageResponse.of(timelineResponsePage);
    }

}
