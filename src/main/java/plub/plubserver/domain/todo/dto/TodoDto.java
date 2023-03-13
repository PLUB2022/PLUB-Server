package plub.plubserver.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.todo.model.Todo;
import plub.plubserver.domain.todo.model.TodoTimeline;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static plub.plubserver.domain.account.dto.AccountDto.AccountInfo;

public class TodoDto {

    private static boolean IsAuthor(Account currentAccount, Todo todo) {
        return currentAccount.getId().equals(todo.getAccount().getId());
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
            List<TodoResponse> todoList
    ) {
        @Builder
        public TodoTimelineResponse {
        }

        public static TodoTimelineResponse of(TodoTimeline todoTimeline, Account currentAccount) {
            List<Todo> todoList = new ArrayList<>();
            List<TodoResponse> todoResponseList = new ArrayList<>();

            for (Todo todo : todoTimeline.getTodoList()) {
                todoList.add(todo);
            }

            for (Todo todo : todoList) {
                todoResponseList.add(TodoResponse.of(todo, IsAuthor(currentAccount, todo)));
            }

            return TodoTimelineResponse.builder()
                    .todoTimelineId(todoTimeline.getId())
                    .totalLikes(todoTimeline.getLikes())
                    .date(todoTimeline.getDate())
                    .isAuthor(IsAuthor(currentAccount, todoTimeline.getTodoList().get(0)))
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
            AccountInfo accountInfo,
            int totalLikes,
            boolean isAuthor,
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
                    .accountInfo(AccountInfo.of(todoList.get(0).getAccount()))
                    .totalLikes(likes)
                    .isAuthor(IsAuthor(currentAccount, todoList.get(0)))
                    .todoList(todoResponseList)
                    .build();
        }

    }


    public record TodoTimelineListResponse(
            List<TodoTimelineResponse> todoTimelineList
    ) {
        @Builder
        public TodoTimelineListResponse {
        }

        public static TodoTimelineListResponse of(List<TodoTimeline> todoTimelineList, Account currentAccount) {
            List<TodoTimelineResponse> todoTimelineResponseList = new ArrayList<>();

            for (TodoTimeline todoTimeline : todoTimelineList) {
                todoTimelineResponseList.add(TodoTimelineResponse.of(todoTimeline, currentAccount));
            }

            return TodoTimelineListResponse.builder()
                    .todoTimelineList(todoTimelineResponseList)
                    .build();
        }
    }


    public record TodoTimelineAllResponse(
            Long todoTimelineId,
            LocalDate date,
            int totalLikes,
            boolean isAuthor,
            AccountInfo accountInfo,
            List<TodoResponse> todoList
    ) {
        @Builder
        public TodoTimelineAllResponse {
        }

        public static TodoTimelineAllResponse of(TodoTimeline todoTimeline, Account account) {
            List<Todo> todoList = new ArrayList<>();
            List<TodoResponse> todoResponseList = new ArrayList<>();

            for (Todo todo : todoTimeline.getTodoList()) {
                todoList.add(todo);
            }

            for (Todo todo : todoList) {
                todoResponseList.add(TodoResponse.of(todo, IsAuthor(account, todo)));
            }

            return TodoTimelineAllResponse.builder()
                    .accountInfo(AccountInfo.of(todoTimeline.getAccount()))
                    .todoTimelineId(todoTimeline.getId())
                    .totalLikes(todoTimeline.getLikes())
                    .isAuthor(IsAuthor(account, todoTimeline.getTodoList().get(0)))
                    .date(todoTimeline.getDate())
                    .todoList(todoResponseList)
                    .build();
        }

    }

    public record TodoTimelineAllPageResponse(
            PageResponse<TodoTimelineAllResponse> response
    ) {
        @Builder
        public TodoTimelineAllPageResponse {
        }

        public static TodoTimelineAllPageResponse of(Page<TodoTimelineAllResponse> todoTimelinePage) {
            return TodoTimelineAllPageResponse.builder()
                    .response(PageResponse.of(todoTimelinePage))
                    .build();
        }

        public static TodoTimelineAllPageResponse ofCursor(PageResponse<TodoTimelineAllResponse> todoTimelinePage) {
            return TodoTimelineAllPageResponse.builder()
                    .response(todoTimelinePage)
                    .build();
        }
    }


    public record TodoTimelinePageResponse(
            AccountInfo accountInfo,
            PageResponse<TodoTimelineResponse> response
    ) {
        @Builder
        public TodoTimelinePageResponse {
        }

        public static TodoTimelinePageResponse of(Page<TodoTimelineResponse> todoTimelinePage, AccountInfo accountInfo) {
            return TodoTimelinePageResponse.builder()
                    .response(PageResponse.of(todoTimelinePage))
                    .accountInfo(accountInfo)
                    .build();
        }

        public static TodoTimelinePageResponse ofCursor(PageResponse<TodoTimelineResponse> todoTimelinePage) {
            return TodoTimelinePageResponse.builder()
                    .response(todoTimelinePage)
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
}
