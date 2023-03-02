package plub.plubserver.domain.feed.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.feed.model.Feed;
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
                .where(getCursorId(cursorId))
                .distinct();

        int size = queryFactory
                .selectFrom(feed)
                .fetch().size();

        log.info("size: {}", size);

        return PageableExecutionUtils.getPage(
                query.orderBy(feed.createdAt.asc())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(feed)
                        .fetch().size());
    }

    private BooleanExpression getCursorId(Long cursorId) {
        return cursorId == null ? null : feed.id.gt(cursorId);
    }
}