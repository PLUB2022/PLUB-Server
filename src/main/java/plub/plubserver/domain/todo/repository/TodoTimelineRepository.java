package plub.plubserver.domain.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.todo.model.TodoTimeline;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoTimelineRepository extends JpaRepository<TodoTimeline, Long>, TodoTimelineRepositoryCustom {
    Optional<TodoTimeline> findByDateAndAccount(LocalDate date, Account account);
    List<TodoTimeline> findByDate(LocalDate date);
    Long countAllByPlubbing(Plubbing plubbing);
    Optional<TodoTimeline> findFirstByPlubbingOrderByDateDesc(Plubbing plubbing);
    Optional<TodoTimeline> findByIdAndAccount(Long timelineId, Account account);
    Optional<TodoTimeline> findByDateAndAccountAndPlubbing(LocalDate date, Account account, Plubbing plubbing);
}
