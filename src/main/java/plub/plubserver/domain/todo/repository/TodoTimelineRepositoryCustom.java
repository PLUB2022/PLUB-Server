package plub.plubserver.domain.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.todo.model.TodoTimeline;

public interface TodoTimelineRepositoryCustom {
    Page<TodoTimeline> findByAccount(Account account, Pageable pageable);
    Page<TodoTimeline> findAllByPlubbing(Plubbing plubbing, Pageable pageable);
}
