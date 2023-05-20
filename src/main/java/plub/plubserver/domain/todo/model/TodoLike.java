package plub.plubserver.domain.todo.model;

import lombok.*;
import org.hibernate.annotations.Where;
import plub.plubserver.common.constant.Visibility;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Where(clause = Visibility.TRUE)
public class TodoLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_timelime_id")
    private TodoTimeline todoTimeline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private boolean isLike;

    public void updateIsLike() {
        isLike = !isLike;
    }
}