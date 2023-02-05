package plub.plubserver.domain.todo.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.todo.model.TodoTimeline;

import static plub.plubserver.domain.todo.model.QTodoTimeline.todoTimeline;

@RequiredArgsConstructor
public class TodoTimelineRepositoryImpl implements TodoTimelineRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TodoTimeline> findByAccount(Account account, Pageable pageable) {
        JPQLQuery<TodoTimeline> query = queryFactory
                .selectFrom(todoTimeline)
                .where(todoTimeline.todoList.any().account.eq(account))
                .orderBy(todoTimeline.date.desc())
                .distinct();
        return PageableExecutionUtils.getPage(
                query.fetch(),
                pageable,
                query::fetchCount
        );
    }
}
