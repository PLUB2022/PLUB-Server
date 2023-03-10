package plub.plubserver.domain.todo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.todo.model.TodoTimeline;

import java.util.List;

public interface TodoTimelineRepositoryCustom {
    Page<TodoTimeline> findByAccount(Account account, Pageable pageable, Long cursorId, String timelineDate);
    Page<TodoTimeline> findAllByPlubbing(Plubbing plubbing, Pageable pageable, Long cursorId, String timelineDate);
    List<TodoTimeline> findByAccountAndPlubbingAndDate(Account account, Long id, int year, int month);
}
