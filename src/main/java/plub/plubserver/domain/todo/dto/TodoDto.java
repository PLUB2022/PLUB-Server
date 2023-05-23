package plub.plubserver.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.todo.model.Todo;
import plub.plubserver.domain.todo.model.TodoLike;
import plub.plubserver.domain.todo.model.TodoTimeline;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static plub.plubserver.domain.account.dto.AccountDto.AccountInfo;
import static plub.plubserver.domain.plubbing.dto.PlubbingDto.PlubbingInfoResponse;

public class TodoDto {

    private static boolean IsAuthor(Account currentAccount, Todo todo) {
        return currentAccount.getId().equals(todo.getAccount().getId());
    }

    private static boolean IsLike(Account currentAccount, TodoTimeline todoTimeLine) {
        if(todoTimeLine.getTodoLikes() == null) {
            return false;
        }
        List<TodoLike> todoLikes = todoTimeLine.getTodoLikes();
        for (TodoLike todoLike : todoLikes) {
            if (todoLike.getAccount().getId().equals(currentAccount.getId())) {
                return todoLike.isLike();
            }
        }
        return false;
    }

    public record CreateTodoRequest(
            @NotBlank @Size(max = 15)
            String content,

            @NotNull
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate date
    ) {
        @Builder
        public CreateTodoRequest {
        }

        public Todo toEntity(Account account) {
            return Todo.builder()
                    .account(account)
                    .content(content)
                    .date(date)
                    .isChecked(false)
                    .isProof(false)
                    .proofImage("")
                    .build();
        }
    }


    public record TodoResponse(
            Long todoId,
            String content,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
            LocalDate date,
            boolean isChecked,
            boolean isProof,
            String proofImage,
            boolean isAuthor
    ) {
        @Builder
        public TodoResponse {
        }

        public static TodoResponse of(Todo todo, boolean isAuthor) {
            return TodoResponse.builder()
                    .todoId(todo.getId())
                    .content(todo.getContent())
                    .date(todo.getDate())
                    .isChecked(todo.isChecked())
                    .isProof(todo.isProof())
                    .proofImage(todo.getProofImage())
                    .isAuthor(isAuthor)
                    .build();
        }
    }


    public record TodoTimelineResponse(
            Long todoTimelineId,
            LocalDate date,
            int totalLikes,
            boolean isAuthor,
            boolean isLike,
            List<TodoResponse> todoList
    ) {
        @Builder
        public TodoTimelineResponse {
        }

        public static TodoTimelineResponse of(TodoTimeline todoTimeline, Account currentAccount, List<Todo> todoList) {
            boolean isAuthor = IsAuthor(currentAccount, todoTimeline.getTodoList().get(0));

            List<TodoResponse> todoResponseList = todoList.stream()
                    .map(todo -> TodoResponse.of(todo, IsAuthor(currentAccount, todo)))
                    .toList();

            return TodoTimelineResponse.builder()
                    .todoTimelineId(todoTimeline.getId())
                    .totalLikes(todoTimeline.getLikeTodo())
                    .date(todoTimeline.getDate())
                    .isAuthor(isAuthor)
                    .isLike(IsLike(currentAccount, todoTimeline))
                    .todoList(todoResponseList)
                    .build();
        }
        public static TodoTimelineResponse ofTemp(LocalDate date) {
            return TodoTimelineResponse.builder()
                    .todoTimelineId(0L)
                    .totalLikes(0)
                    .date(date)
                    .isAuthor(true)
                    .todoList(new ArrayList<>())
                    .build();
        }
    }


    public record TodoListResponse(
            Long todoTimelineId,
            AccountInfo accountInfo,
            int totalLikes,
            boolean isAuthor,
            boolean isLike,
            List<TodoResponse> todoList
    ) {
        @Builder
        public TodoListResponse {
        }

        public static TodoListResponse of(List<Todo> todoList, Account currentAccount, int likes) {
            List<TodoResponse> todoResponseList = new ArrayList<>();

            for (Todo todo : todoList) {
                todoResponseList.add(TodoResponse.of(todo, IsAuthor(currentAccount, todo)));
            }

            return TodoListResponse.builder()
                    .todoTimelineId(todoList.get(0).getTodoTimeline().getId())
                    .accountInfo(AccountInfo.of(todoList.get(0).getAccount()))
                    .totalLikes(likes)
                    .isAuthor(IsAuthor(currentAccount, todoList.get(0)))
                    .isLike(IsLike(currentAccount, todoList.get(0).getTodoTimeline()))
                    .todoList(todoResponseList)
                    .build();
        }

    }


    public record TodoTimelineAllResponse(
            Long todoTimelineId,
            LocalDate date,
            int totalLikes,
            boolean isAuthor,
            boolean isLike,
            AccountInfo accountInfo,
            List<TodoResponse> todoList
    ) {
        @Builder
        public TodoTimelineAllResponse {
        }

        public static TodoTimelineAllResponse of(TodoTimeline todoTimeline, Account currentAccount, List<Todo> todoList) {

            List<TodoResponse> todoResponseList = todoList
                    .stream()
                    .map(todo -> TodoResponse.of(todo, IsAuthor(currentAccount, todo)))
                    .toList();

            return TodoTimelineAllResponse.builder()
                    .accountInfo(AccountInfo.of(todoTimeline.getAccount()))
                    .todoTimelineId(todoTimeline.getId())
                    .totalLikes(todoTimeline.getLikeTodo())
                    .isAuthor(IsAuthor(currentAccount, todoTimeline.getTodoList().get(0)))
                    .isLike(IsLike(currentAccount, todoTimeline))
                    .date(todoTimeline.getDate())
                    .todoList(todoResponseList)
                    .build();
        }

    }


    public record UpdateTodoRequest(
            @NotBlank @Size(max = 15)
            String content,

            @NotNull
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            LocalDate date
    ) {
        @Builder
        public UpdateTodoRequest {
        }

        public Todo toEntity(Todo todo) {
            return Todo.builder()
                    .id(todo.getId())
                    .account(todo.getAccount())
                    .content(content)
                    .date(date)
                    .isChecked(todo.isChecked())
                    .isProof(todo.isProof())
                    .proofImage(todo.getProofImage())
                    .build();
        }
    }

    public record TodoMessage(Object result) {
    }

    public record TodoIdResponse(Long todoId) {
    }

    public record ProofTodoRequest(
            String proofImage
    ) {
        @Builder
        public ProofTodoRequest {
        }
    }

    public record TodoTimelineDateResponse(
            List<String> dateList
    ) {
        @Builder
        public TodoTimelineDateResponse {
        }

        public static TodoTimelineDateResponse of(List<TodoTimeline> todoTimelineList) {
            List<String> dateList = new ArrayList<>();

            for (TodoTimeline todoTimeline : todoTimelineList) {
                String format = todoTimeline.getDate().format(DateTimeFormatter.ofPattern("dd"));
                dateList.add(format);
            }

            return TodoTimelineDateResponse.builder()
                    .dateList(dateList)
                    .build();
        }

    }

    public record MyTodoListResponse(
            PlubbingInfoResponse plubbingInfo,
            PageResponse<TodoTimelineResponse> todoTimelineResponse
    ) {
        @Builder
        public MyTodoListResponse {
        }

        public static MyTodoListResponse of(Plubbing plubbing, PageResponse<TodoTimelineResponse> todoTimelineResponse) {
            return MyTodoListResponse.builder()
                    .plubbingInfo(PlubbingInfoResponse.of(plubbing))
                    .todoTimelineResponse(todoTimelineResponse)
                    .build();
        }
    }
}
