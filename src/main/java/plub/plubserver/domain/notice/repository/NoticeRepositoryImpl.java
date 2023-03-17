package plub.plubserver.domain.notice.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.plubbing.model.Plubbing;

import static plub.plubserver.domain.notice.model.QNotice.notice;


@RequiredArgsConstructor
public class NoticeRepositoryImpl implements NoticeRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Notice> findAllByPlubbingAndVisibilityCursor(
            Plubbing plubbing,
            boolean visibility,
            Pageable pageable,
            Long cursorId
    ) {
        JPQLQuery<Notice> query = queryFactory
                .selectFrom(notice)
                .where(notice.plubbing.eq(plubbing),
                        notice.visibility.eq(visibility),
                        getCursorId(cursorId))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(notice.id.desc())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(notice)
                        .fetch().size());
    }

    private BooleanExpression getCursorId(Long cursorId) {
        if (cursorId == null || cursorId == 0) return null;
        return notice.id.lt(cursorId);
    }
}