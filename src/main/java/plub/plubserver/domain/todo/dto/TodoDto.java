package plub.plubserver.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import plub.plubserver.common.dto.PageResponse;
import plub.plubserver.domain.account.dto.AccountDto;
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

public class TodoDto {

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
                    .likes(0)
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
            int likes
    ) {
        @Builder
        public TodoResponse {
        }

        public static TodoResponse of(Todo todo) {
            return TodoResponse.builder()
                    .todoId(todo.getId())
                    .content(todo.getContent())
                    .date(todo.getDate())
                    .isChecked(todo.isChecked())
                    .isProof(todo.isProof())
                    .proofImage(todo.getProofImage())
                    .likes(todo.getLikes())
                    .build();
        }
    }


    public record TodoTimelineResponse(
            Long todoTimelineId,
            LocalDate date,
            int totalLikes,
            List<TodoResponse> todoList
    ) {
        @Builder
        public TodoTimelineResponse {
        }

        public static TodoTimelineResponse of(TodoTimeline todoTimeline) {
            List<Todo> todoList = new ArrayList<>();
            List<TodoResponse> todoResponseList = new ArrayList<>();
            int totalLikes = 0;

            for (Todo todo : todoTimeline.getTodoList()) {
                todoList.add(todo);
                totalLikes += todo.getLikes();
            }

            for (Todo todo : todoList) {
                todoResponseList.add(TodoResponse.of(todo));
            }

            return TodoTimelineResponse.builder()
                    .todoTimelineId(todoTimeline.getId())
                    .totalLikes(totalLikes)
                    .date(todoTimeline.getDate())
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

        public static TodoTimelineListResponse of(List<TodoTimeline> todoTimelineList) {
            List<TodoTimelineResponse> todoTimelineResponseList = new ArrayList<>();

            for (TodoTimeline todoTimeline : todoTimelineList) {
                todoTimelineResponseList.add(TodoTimelineResponse.of(todoTimeline));
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
            AccountDto.AccountInfo accountInfo,
            List<TodoResponse> todoList
    ) {
        @Builder
        public TodoTimelineAllResponse {
        }

        public static TodoTimelineAllResponse of(TodoTimeline todoTimeline) {
            List<Todo> todoList = new ArrayList<>();
            List<TodoResponse> todoResponseList = new ArrayList<>();
            int totalLikes = 0;

            for (Todo todo : todoTimeline.getTodoList()) {
                todoList.add(todo);
                totalLikes += todo.getLikes();
            }

            for (Todo todo : todoList) {
                todoResponseList.add(TodoResponse.of(todo));
            }

            return TodoTimelineAllResponse.builder()
                    .accountInfo(AccountDto.AccountInfo.of(todoTimeline.getAccount()))
                    .todoTimelineId(todoTimeline.getId())
                    .totalLikes(totalLikes)
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
            AccountDto.AccountInfo accountInfo,
            PageResponse<TodoTimelineResponse> response
    ) {
        @Builder
        public TodoTimelinePageResponse {
        }

        public static TodoTimelinePageResponse of(Page<TodoTimelineResponse> todoTimelinePage, AccountDto.AccountInfo accountInfo) {
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
                    .likes(todo.getLikes())
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
