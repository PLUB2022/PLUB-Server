package plub.plubserver.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.dto.AccountDto;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;
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
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoTimelineRepository todoTimelineRepository;
    private final PlubbingService plubbingService;
    private final AccountRepository accountRepository;

    @Transactional
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
                });

        return new TodoIdResponse(todo.getId());
    }

    // 투두 상세 조회
    public TodoResponse getTodo(Long plubbingId, Long todoId) {
        plubbingService.getPlubbing(plubbingId);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
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
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
        todoRepository.delete(todo);
        return new TodoMessage("투두 삭제 성공");
    }

    // 투두 업데이트
    @Transactional
    public TodoResponse updateTodo(Account currentAccount, Long plubbingId, Long todoId, UpdateTodoRequest request) {
        plubbingService.getPlubbing(plubbingId);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
        if (todo.isChecked())
            throw new TodoException(StatusCode.ALREADY_CHECKED_TODO);

        todo.updateTodoDateAndContent(request.date(), request.content());
        return TodoResponse.of(todo);
    }

    // 투두 완료
    @Transactional
    public TodoIdResponse completeTodo(Account currentAccount, Long plubbingId, Long todoId) {
        plubbingService.getPlubbing(plubbingId);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
        todo.updateTodoIsChecked(true);
        return new TodoIdResponse(todo.getId());
    }

    // 투두 완료 취소
    @Transactional
    public TodoIdResponse cancelTodo(Account currentAccount, Long plubbingId, Long todoId) {
        plubbingService.getPlubbing(plubbingId);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
        if (todo.isProof())
            throw new TodoException(StatusCode.ALREADY_PROOF_TODO);
        todo.updateTodoIsChecked(false);
        return new TodoIdResponse(todo.getId());
    }

    // 투두 인증
    @Transactional
    public TodoResponse proofTodo(Account currentAccount, Long plubbingId, Long todoId, ProofTodoRequest proofImage) {
        plubbingService.getPlubbing(plubbingId);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
        if (todo.isChecked()) {
            todo.updateTodoProofImage(proofImage.proofImage());
            todo.updateTodoIsProof(true);
            return TodoResponse.of(todo);
        } else if (todo.isProof()) {
            throw new TodoException(StatusCode.ALREADY_PROOF_TODO);
        } else {
            throw new TodoException(StatusCode.NOT_COMPLETE_TODO);
        }
    }

    // 회원 타임라인 조회
    public TodoTimelinePageResponse getAccountTodoTimelinePage(Long plubbingId, Long accountId, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException(StatusCode.NOT_FOUND_ACCOUNT));
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

    // 회원 타임라인 날짜 조회
    public TodoTimelineDateResponse getTodoCalendarDateList(Account currentAccount, Long plubbingId, int year, int month) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        List<TodoTimeline> byAccountAndPlubbingAndDate = todoTimelineRepository.findByAccountAndPlubbingAndDate(currentAccount, plubbing.getId(), year, month);
        return TodoTimelineDateResponse.of(byAccountAndPlubbingAndDate);
    }

}
