package plub.plubserver.domain.feed.repository;

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
    public Page<FeedComment> findAllByFeed(Feed feed, Pageable pageable) {
        return PageableExecutionUtils.getPage(
                queryFactory
                        .selectFrom(feedComment)
                        .where(feedComment.feed.eq(feed),
                                feedComment.visibility.eq(true))
                        .orderBy(feedComment.groupId.desc(),
                                feedComment.depth.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .distinct()
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(feedComment)
                        .where(feedComment.feed.eq(feed),
                                feedComment.visibility.eq(true))
                        .orderBy(feedComment.groupId.desc(),
                                feedComment.depth.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .distinct()
                        .fetch().size()
        );
    }
}