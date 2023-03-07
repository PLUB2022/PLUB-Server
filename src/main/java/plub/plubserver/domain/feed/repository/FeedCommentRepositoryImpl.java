package plub.plubserver.domain.feed.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedComment;

import static plub.plubserver.domain.feed.model.QFeedComment.feedComment;

@RequiredArgsConstructor
public class FeedCommentRepositoryImpl implements FeedCommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<FeedComment> findAllByFeed(
            Feed feed,
            Pageable pageable,
            Long cursorId
    ) {
        JPQLQuery<FeedComment> query = queryFactory
                .selectFrom(feedComment)
                .where(feedComment.feed.eq(feed),
                        feedComment.visibility.eq(true),
                        getCursorId(cursorId))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(feedComment.commentGroupId.asc(),
                                feedComment.createdAt.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory.selectFrom(feedComment)
                        .fetch().size());
    }

    private BooleanExpression getCursorId(Long cursorId) {
        return cursorId == null ? null : feedComment.id.gt(cursorId);
    }
}