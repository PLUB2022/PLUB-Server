package plub.plubserver.domain.calendar.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.calendar.model.Calendar;

import static plub.plubserver.domain.calendar.model.QCalendar.calendar;


@RequiredArgsConstructor
public class CalendarRepositoryImpl implements CalendarRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Calendar> findAllByPlubbingId(
            Long plubbingId,
            Pageable pageable,
            Long cursorId,
            String startedAt
    ) {
        JPQLQuery<Calendar> query = queryFactory
                .selectFrom(calendar)
                .where(calendar.plubbing.id.eq(plubbingId),
                        calendar.visibility.eq(true),
                        getCursorId(startedAt, cursorId))
                .orderBy(calendar.startedAt.desc(), calendar.id.desc())
                .distinct();


        return PageableExecutionUtils.getPage(
                query.orderBy(calendar.startedAt.desc(), calendar.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory.selectFrom(calendar)
                        .fetch().size());
    }

    private BooleanExpression getCursorId(String startedAt, Long cursorId) {
        return cursorId == null ? null : calendar.startedAt.lt(startedAt)
                .and(calendar.id.gt(cursorId))
                .or(calendar.startedAt.lt(startedAt));
    }
}
