package plub.plubserver.domain.todo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_timelime_id")
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    // 타임라인(1) - 투두(다)
    @OneToMany(mappedBy = "todoTimeline", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todoList = new ArrayList<>();

    // 타임라인(다) - 모임(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubbing_id")
    private Plubbing plubbing;

    // 타임라인(다) - 계정(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 투두(1) - 좋아요(다)
    @OneToMany(mappedBy = "todoTimeline", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TodoLike> todoLikes = new ArrayList<>();

    private int likeTodo;

    public void updateTodo(Todo todo) {
        if (todoList == null) todoList = new ArrayList<>();
        todoList.add(todo);
        account = todo.getAccount();
        date = todo.getDate();
        todo.updateTodoTimeline(this);
    }

    public void addLike() {
        this.likeTodo++;
    }

    public void subLike() {
        this.likeTodo--;
    }
}
