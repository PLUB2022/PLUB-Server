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
            Long lastCommentGroupId,
            Long lastCommentId
    ) {
        JPQLQuery<FeedComment> query = queryFactory
                .selectFrom(feedComment)
                .where(feedComment.feed.eq(feed),
                        feedComment.visibility.eq(true),
                        getCursorId(lastCommentGroupId, lastCommentId))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(feedComment.commentGroupId.asc(),
                                feedComment.id.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory.selectFrom(feedComment)
                        .fetch().size());
    }

    private BooleanExpression getCursorId(Long lastCommentGroupId, Long lastCommentId) {
        if (lastCommentGroupId == null || lastCommentId == null) {
            return null;
        }
        return feedComment.commentGroupId.goe(lastCommentGroupId)
                .and(feedComment.id.gt(lastCommentId))
                .or(feedComment.commentGroupId.gt(lastCommentGroupId));
    }
}