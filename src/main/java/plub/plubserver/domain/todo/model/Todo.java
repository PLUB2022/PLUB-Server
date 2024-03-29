package plub.plubserver.domain.todo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;
import plub.plubserver.common.constant.Visibility;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = Visibility.TRUE)
public class Todo extends BaseEntity {
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

    // 투두(다) - 회원(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    // 투두(다) - 타임라인(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_timeline_id")
    private TodoTimeline todoTimeline;

    private LocalDateTime checkAt;

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

    public void updateTodoCheckAt() {
        LocalDateTime now = LocalDateTime.now();
        this.checkAt = now;
    }
}
