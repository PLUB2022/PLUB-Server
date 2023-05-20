package plub.plubserver.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.service.PlubbingService;
import plub.plubserver.domain.todo.exception.TodoException;
import plub.plubserver.domain.todo.model.Todo;
import plub.plubserver.domain.todo.model.TodoLike;
import plub.plubserver.domain.todo.model.TodoTimeline;
import plub.plubserver.domain.todo.repository.TodoLikeRepository;
import plub.plubserver.domain.todo.repository.TodoRepository;
import plub.plubserver.domain.todo.repository.TodoTimelineRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static plub.plubserver.domain.todo.dto.TodoDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoTimelineRepository todoTimelineRepository;
    private final PlubbingService plubbingService;
    private final AccountRepository accountRepository;
    private final TodoLikeRepository todoLikeRepository;


    private Todo getTodoById(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
    }

    public TodoTimeline getTodoTimeline(Long todoTimelineId) {
        return todoTimelineRepository.findById(todoTimelineId).orElseThrow(
                () -> new TodoException(StatusCode.NOT_FOUNT_TODO_TIMELINE));
    }

    public Todo getTodo(Long todoId) {
        return todoRepository.findById(todoId).orElseThrow(
                () -> new TodoException(StatusCode.NOT_FOUNT_TODO));
    }

    @Transactional
    public TodoResponse createTodo(Account currentAccount, Long plubbingId, CreateTodoRequest request) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        Todo todo = request.toEntity(currentAccount);

        Optional<TodoTimeline> todoTimelineOptional = todoTimelineRepository
                .findByDateAndAccount(request.date(), currentAccount);

        TodoTimeline todoTimeline;
        if (todoTimelineOptional.isPresent()) {
            todoTimeline = todoTimelineOptional.get();
            updateTodoTimeline(todoTimeline, todo);
        } else {
            createTodoTimeline(request, currentAccount, plubbing, todo);
        }

        return TodoResponse.of(todo, true);
    }

    private void updateTodoTimeline(TodoTimeline todoTimeline, Todo todo) {
        todoCountCheck(todoTimeline);
        todo = todoRepository.save(todo);
        todo.updateTodoCheckAt();
        todoTimeline.updateTodo(todo);
        todoTimelineRepository.save(todoTimeline);
    }

    private TodoTimeline createTodoTimeline(CreateTodoRequest request, Account currentAccount, Plubbing plubbing, Todo todo) {
        TodoTimeline todoTimeline = TodoTimeline.builder()
                .date(request.date())
                .account(currentAccount)
                .plubbing(plubbing)
                .todoList(List.of(todo))
                .likeTodo(0)
                .build();
        todo.updateTodoTimeline(todoTimeline);
        todo.updateTodoCheckAt();
        todoTimeline = todoTimelineRepository.save(todoTimeline);
        todoRepository.save(todo);
        return todoTimeline;
    }


    private void todoCountCheck(TodoTimeline timeline) {
        if (timeline.getTodoList().size() >= 5) {
            throw new TodoException(StatusCode.TOO_MANY_TODO);
        }
    }

    // 투두 상세 조회
    public TodoResponse getTodo(Account currentAccount, Long plubbingId, Long todoId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
        boolean isAuthor = IsAuthor(currentAccount, todo);
        return TodoResponse.of(todo, isAuthor);
    }

    // 작성자인지 검증
    private boolean IsAuthor(Account currentAccount, Todo todo) {
        return currentAccount.getId().equals(todo.getAccount().getId());
    }

    // 투두 리스트 상세 조회
    public TodoListResponse getTodoTimelineList(Account currentAccount, Long plubbingId, Long todoTimelineId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);
        List<Todo> todoList = todoRepository.findAllByTodoTimelineAndPlubbing(getTodoTimeline(todoTimelineId), plubbing);
        int likes = todoTimelineRepository.findById(todoTimelineId)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO_TIMELINE))
                .getLikeTodo();
        return TodoListResponse.of(todoList, currentAccount, likes);
    }


    // 투두 타임라인 조회 (날짜)
    public TodoTimelineResponse getTodoTimeline(Account currentAccount, Long plubbingId, LocalDate date) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);
        Optional<TodoTimeline> todoTimeline = todoTimelineRepository.findByDateAndAccountAndPlubbing(date, currentAccount, plubbing);

        return todoTimeline.map(timeline ->
        {
            List<Todo> todoList = todoRepository.findAllByTodoTimelineAndPlubbing(timeline, plubbing);
            return TodoTimelineResponse.of(timeline, currentAccount, todoList);
        }).orElseGet(() -> TodoTimelineResponse.ofTemp(date));
    }

    @Transactional
    public TodoMessage deleteTodoList(Account currentAccount, Long plubbingId, Long todoId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);

        Todo todo = getTodoById(todoId);
        TodoTimeline todoTimeline = todo.getTodoTimeline();

        if (isLastTodoInTimeline(todoTimeline)) {
            todoTimeline.softDelete();
        }

        todo.softDelete();

        return new TodoMessage("투두 삭제 성공");
    }

    private boolean isLastTodoInTimeline(TodoTimeline todoTimeline) {
        long count = todoTimeline.getTodoList().stream()
                .filter(BaseEntity::isVisibility)
                .count();
        return count == 1;
    }


    // 투두 업데이트
    @Transactional
    public TodoResponse updateTodo(Account currentAccount, Long plubbingId, Long todoId, UpdateTodoRequest request) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
        if (todo.isChecked())
            throw new TodoException(StatusCode.ALREADY_CHECKED_TODO);

        todo.updateTodoDateAndContent(request.date(), request.content());
        boolean isAuthor = IsAuthor(currentAccount, todo);
        return TodoResponse.of(todo, isAuthor);
    }

    // 투두 완료
    @Transactional
    public TodoResponse completeTodo(Account currentAccount, Long plubbingId, Long todoId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
        todo.updateTodoIsChecked(true);
        todo.updateTodoCheckAt();
        return TodoResponse.of(todo, true);
    }

    // 투두 완료 취소
    @Transactional
    public TodoResponse cancelTodo(Account currentAccount, Long plubbingId, Long todoId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
        if (todo.isProof())
            throw new TodoException(StatusCode.ALREADY_PROOF_TODO);
        todo.updateTodoIsChecked(false);
        todo.updateTodoCheckAt();
        return TodoResponse.of(todo, true);
    }

    // 투두 인증
    @Transactional
    public TodoResponse proofTodo(Account currentAccount, Long plubbingId, Long todoId, ProofTodoRequest proofImage) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);
        Todo todo = todoRepository.findByIdAndAccount(todoId, currentAccount)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
        if (todo.isChecked()) {
            todo.updateTodoProofImage(proofImage.proofImage());
            todo.updateTodoIsProof(true);
            boolean isAuthor = IsAuthor(currentAccount, todo);
            return TodoResponse.of(todo, isAuthor);
        } else if (todo.isProof()) {
            throw new TodoException(StatusCode.ALREADY_PROOF_TODO);
        } else {
            throw new TodoException(StatusCode.NOT_COMPLETE_TODO);
        }
    }

    // 내 타임라인 조회
    public MyTodoListResponse getMyTodoTimelinePage(
            Account account,
            Long plubbingId,
            Pageable pageable,
            Long cursorId
    ) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        PageResponse<TodoTimelineResponse> response = getTodoTimelinePageResponse(plubbingId, pageable, account, cursorId);
        return MyTodoListResponse.of(plubbing, response);
    }

    // 회원 타임라인 조회
    public PageResponse<TodoTimelineResponse> getAccountTodoTimelinePage(
            Account currentAccount,
            Long plubbingId,
            Long accountId,
            Pageable pageable,
            Long cursorId
    ) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException(StatusCode.NOT_FOUND_ACCOUNT));
        return getTodoTimelinePageResponse(plubbingId, pageable, account, cursorId);
    }

    private PageResponse<TodoTimelineResponse> getTodoTimelinePageResponse(
            Long plubbingId,
            Pageable pageable,
            Account account,
            Long cursorId
    ) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(account, plubbing);

        Long nextCursorId = cursorId;
        if (cursorId != null && cursorId == 0) {
            Optional<TodoTimeline> first = todoTimelineRepository.findFirstByPlubbingOrderByDateDesc(plubbing);
            nextCursorId = first.map(TodoTimeline::getId).orElse(null);
        }
        String date = nextCursorId == null ? null : getTodoTimeline(nextCursorId).getDate().toString();

        Page<TodoTimelineResponse> todoTimelinePage =
                todoTimelineRepository.findByAccountAndPlubbing(account, plubbing, pageable, cursorId, date)
                        .map(todoTimeline -> {
                            List<Todo> todoList = todoRepository.findAllByTodoTimelineAndPlubbing(todoTimeline, plubbing);
                            return TodoTimelineResponse.of(todoTimeline, account, todoList);
                        });

        Long totalElements = todoTimelineRepository.countAllByPlubbing(plubbing);
        return PageResponse.ofCursor(todoTimelinePage, totalElements);
    }

    // 타임라인 전체 조회
    public PageResponse<TodoTimelineAllResponse> getAllTodoList(Account currentAccount, Long plubbingId, Pageable pageable, Long cursorId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);

        Long nextCursorId = cursorId;
        if (cursorId != null && cursorId == 0) {
            Optional<TodoTimeline> first = todoTimelineRepository.findFirstByPlubbingOrderByDateDesc(plubbing);
            if (first.isEmpty()) {
                nextCursorId = null;
            } else {
                nextCursorId = first.map(TodoTimeline::getId).orElse(null);
            }
        }
        String date = nextCursorId == null ? null : getTodoTimeline(nextCursorId).getDate().toString();

        Page<TodoTimelineAllResponse> timelineResponsePage =
                todoTimelineRepository.findAllByPlubbing(plubbing, pageable, cursorId, date)
                        .map(todoTimeline ->
                        {
                            List<Todo> todoList = todoRepository.findAllByTodoTimelineAndPlubbing(todoTimeline, plubbing);
                            return TodoTimelineAllResponse.of(todoTimeline, currentAccount, todoList);
                        });

        Long totalElements = todoTimelineRepository.countAllByPlubbing(plubbing);
        return PageResponse.ofCursor(timelineResponsePage, totalElements);
    }

    // 회원 타임라인 날짜 조회
    public TodoTimelineDateResponse getTodoCalendarDateList(Account currentAccount, Long plubbingId, int year, int month) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);
        List<TodoTimeline> byAccountAndPlubbingAndDate = todoTimelineRepository.findByAccountAndPlubbingAndDate(currentAccount, plubbing.getId(), year, month);
        return TodoTimelineDateResponse.of(byAccountAndPlubbingAndDate);
    }

    // 투두 좋아요
    @Transactional
    public TodoTimelineResponse likeTodo(Account currentAccount, Long plubbingId, Long timelineId) {
        Plubbing plubbing = plubbingService.getPlubbing(plubbingId);
        plubbingService.checkMember(currentAccount, plubbing);
        TodoTimeline todoTimeline = todoTimelineRepository.findByIdAndPlubbing(timelineId, plubbing)
                .orElseThrow(() -> new TodoException(StatusCode.NOT_FOUNT_TODO));
        todoLikeRepository.findByAccountAndTodoTimeline(currentAccount, todoTimeline)
                .ifPresentOrElse(todoLike -> {
                    if (todoLike.isLike()) {
                        todoLike.updateIsLike();
                        todoTimeline.subLike();
                    } else {
                        todoLike.updateIsLike();
                        todoTimeline.addLike();
                    }
                }, () -> {
                    TodoLike todoLike = todoLikeRepository.save(TodoLike.builder()
                            .account(currentAccount)
                            .todoTimeline(todoTimeline)
                            .build());
                    todoTimeline.addLike();
                    todoLike.updateIsLike();
                });
        List<Todo> todoList = todoRepository.findAllByTodoTimelineAndPlubbing(todoTimeline, plubbing);
        return TodoTimelineResponse.of(todoTimeline, currentAccount, todoList);
    }

}
