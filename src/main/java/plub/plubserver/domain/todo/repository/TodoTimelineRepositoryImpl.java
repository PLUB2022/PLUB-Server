package plub.plubserver.domain.todo.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.model.QPlubbing;
import plub.plubserver.domain.todo.model.TodoTimeline;

import java.time.LocalDate;
import java.util.List;

import static plub.plubserver.domain.todo.model.QTodoTimeline.todoTimeline;

@RequiredArgsConstructor
public class TodoTimelineRepositoryImpl implements TodoTimelineRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TodoTimeline> findByAccount(Account account, Pageable pageable) {
        LocalDate now = LocalDate.now();
        List<TodoTimeline> fetch1 = queryFactory
                .selectFrom(todoTimeline)
                .where(todoTimeline.todoList.any().account.eq(account), todoTimeline.date.loe(now))
                .orderBy(todoTimeline.date.desc())
                .distinct()
                .fetch();

        LocalDate nextMonth = now.plusMonths(3);
        List<TodoTimeline> fetch2 = queryFactory
                .selectFrom(todoTimeline)
                .where(todoTimeline.todoList.any().account.eq(account),
                        todoTimeline.date.gt(now), todoTimeline.date.loe(nextMonth))
                .orderBy(todoTimeline.date.asc())
                .distinct()
                .limit(3)
                .fetch();

        fetch1.addAll(fetch2);
        fetch1.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

        return PageableExecutionUtils.getPage(
                fetch1,
                pageable,
                fetch1::size
        );
    }

    @Override
    public Page<TodoTimeline> findAllByPlubbing(Plubbing plubbing, Pageable pageable) {
        LocalDate now = LocalDate.now();
        List<TodoTimeline> fetch1 = queryFactory
                .selectFrom(todoTimeline)
                .leftJoin(todoTimeline.plubbing, QPlubbing.plubbing)
                .fetchJoin()
                .where(todoTimeline.plubbing.eq(plubbing), todoTimeline.date.loe(now))
                .orderBy(todoTimeline.date.desc())
                .distinct()
                .fetch();

        LocalDate nextMonth = now.plusMonths(3);
        List<TodoTimeline> fetch2 = queryFactory
                .selectFrom(todoTimeline)
                .leftJoin(todoTimeline.plubbing, QPlubbing.plubbing)
                .fetchJoin()
                .where(todoTimeline.plubbing.eq(plubbing),
                        todoTimeline.date.gt(now), todoTimeline.date.loe(nextMonth))
                .orderBy(todoTimeline.date.asc())
                .distinct()
                .limit(3)
                .fetch();

        fetch1.addAll(fetch2);
        fetch1.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));


        return PageableExecutionUtils.getPage(
                fetch1,
                pageable,
                fetch1::size
        );
    }

    @Override
    public List<TodoTimeline> findByAccountAndPlubbingAndDate(Account account, Long id, int year, int month) {
        return queryFactory
                .selectFrom(todoTimeline)
                .where(todoTimeline.todoList.any().account.eq(account), todoTimeline.plubbing.id.eq(id),
                        todoTimeline.date.year().eq(year),
                        todoTimeline.date.month().eq(month))
                .orderBy(todoTimeline.date.desc())
                .distinct()
                .fetch();
    }
}
