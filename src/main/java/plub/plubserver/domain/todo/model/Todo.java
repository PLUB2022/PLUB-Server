package plub.plubserver.domain.todo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import plub.plubserver.domain.account.model.Account;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private boolean isChecked;
    private boolean isProof;
    private String proofImage;

    private int likes;

    // 투두(다) - 회원(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 투두(1) - 타임라인(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_timeline_id")
    private TodoTimeline todoTimeline;

    public void updateTodoDateAndContent(LocalDate date, String content) {
        this.date = date;
        this.content = content;
    }

    public void updateTodoIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void updateTodoIsProof(boolean isProof) {
        this.isProof = isProof;
    }

    public void updateTodoProofImage(String proofImage) {
        this.proofImage = proofImage;
    }


    public void updateTodoTimeline(TodoTimeline todoTimeline) {
        this.todoTimeline = todoTimeline;
    }
}
