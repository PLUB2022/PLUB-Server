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
            Long cursorId
    ) {
        JPQLQuery<Calendar> query = queryFactory
                .selectFrom(calendar)
                .where(calendar.plubbing.id.eq(plubbingId),
                        calendar.visibility.eq(true),
                        getCursorId(cursorId))
                .distinct();


        return PageableExecutionUtils.getPage(
                query.orderBy(calendar.createdAt.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory.selectFrom(calendar)
                        .fetch().size());
    }

    private BooleanExpression getCursorId(Long cursorId) {
        return cursorId == null ? null : calendar.id.gt(cursorId);
    }
}
