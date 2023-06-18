package plub.plubserver.domain.feed.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.ViewType;
import plub.plubserver.domain.plubbing.model.Plubbing;

import static plub.plubserver.domain.feed.model.QFeed.feed;

@Slf4j
@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Feed> findAllByPlubbingAndPinAndVisibilityCursor(
            Plubbing plubbing,
            Boolean pin,
            Boolean visibility,
            Pageable pageable,
            Long cursorId
    ) {
        JPQLQuery<Feed> query = queryFactory
                .selectFrom(feed)
                .where(feed.plubbing.eq(plubbing),
                        feed.pin.eq(pin),
                        feed.visibility.eq(visibility),
                        getCursorId(cursorId))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(feed.id.desc())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(feed)
                        .fetch().size());
    }

    @Override
    public Page<Feed> findAllByPlubbingAndAccountAndVisibilityAndViewType(
            Plubbing plubbing,
            Account account,
            Boolean visibility,
            ViewType viewType,
            Pageable pageable,
            Long cursorId
    ) {
        JPQLQuery<Feed> query = queryFactory
                .selectFrom(feed)
                .where(feed.plubbing.eq(plubbing),
                        feed.account.eq(account),
                        feed.visibility.eq(visibility),
                        feed.viewType.eq(ViewType.NORMAL),
                        getCursorId(cursorId))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(feed.id.desc())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(feed)
                        .fetch().size());
    }

    private BooleanExpression getCursorId(Long cursorId) {
        if (cursorId == null || cursorId == 0) return null;
        else return feed.id.lt(cursorId);
    }
}