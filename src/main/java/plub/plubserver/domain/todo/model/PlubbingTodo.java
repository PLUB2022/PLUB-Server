package plub.plubserver.domain.todo.model;

import lombok.*;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlubbingTodo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    private String content;
    private String date;

    private boolean isChecked;
    private boolean isProof;
    private String proofImage;

    private int likes;

    // 타임라인(다) - 모임(1)
    @ManyToOne
    @JoinColumn(name = "plubbing_id")
    private Plubbing plubbing;

}
