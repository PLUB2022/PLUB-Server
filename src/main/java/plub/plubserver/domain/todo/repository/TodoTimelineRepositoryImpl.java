package plub.plubserver.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TodoTimelineRepositoryImpl implements TodoTimelineRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TodoTimeline> findByAccountAndPlubbing(
            Account account,
            Plubbing plubbing,
            Pageable pageable,
            Long cursorId,
            String date
    ) {
        LocalDate now = LocalDate.now();
        JPAQuery<TodoTimeline> query = queryFactory
                .selectFrom(todoTimeline)
                .leftJoin(todoTimeline.plubbing, QPlubbing.plubbing)
                .fetchJoin()
                .where(
                        todoTimeline.plubbing.eq(plubbing),
                        todoTimeline.date.loe(now),
                        getCursorId(cursorId, date)
                )
                .orderBy(todoTimeline.date.desc(), todoTimeline.id.desc())
                .distinct();


        return PageableExecutionUtils.getPage(
                query.orderBy(todoTimeline.id.desc())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(todoTimeline)
                        .fetch().size());
    }

    @Override
    public Page<TodoTimeline> findAllByPlubbing(
            Plubbing plubbing,
            Pageable pageable,
            Long cursorId,
            String date
    ) {
        LocalDate now = LocalDate.now();
        List<TodoTimeline> fetch1 = queryFactory
                .selectFrom(todoTimeline)
                .leftJoin(todoTimeline.plubbing)
                .fetchJoin()
                .where(
                        todoTimeline.plubbing.eq(plubbing),
                        todoTimeline.date.loe(now),
                        getCursorId(cursorId, date)
                )
                .orderBy(todoTimeline.date.desc(), todoTimeline.id.desc())
                .distinct()
                .limit(pageable.getPageSize())
                .fetch();

        LocalDate nextMonth = now.plusMonths(3);
        List<TodoTimeline> fetch2 = queryFactory
                .selectFrom(todoTimeline)
                .leftJoin(todoTimeline.plubbing)
                .fetchJoin()
                .where(
                        todoTimeline.plubbing.eq(plubbing),
                        todoTimeline.date.gt(now),
                        todoTimeline.date.loe(nextMonth)
                )
                .orderBy(todoTimeline.date.asc())
                .distinct()
                .limit(3)
                .fetch();

        fetch1.addAll(fetch2);
        fetch1.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

        return PageableExecutionUtils.getPage(
                fetch1,
                pageable,
                () -> queryFactory
                        .selectFrom(todoTimeline)
                        .fetch().size()
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

    private BooleanExpression getCursorId(Long cursorId, String date) {
        return date == null || cursorId == null ? null : todoTimeline.date.lt(LocalDate.parse(date))
                .and(todoTimeline.id.gt(cursorId))
                .or(todoTimeline.date.lt(LocalDate.parse(date)));
    }

    private BooleanExpression getCursorDate(String cursorDate) {
        return cursorDate == null ? null : todoTimeline.date.loe(LocalDate.parse(cursorDate));
    }
}
