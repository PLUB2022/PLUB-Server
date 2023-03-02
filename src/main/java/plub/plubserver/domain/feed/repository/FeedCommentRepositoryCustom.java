package plub.plubserver.domain.feed.repository;

import org.springframework.data.domain.Page;
import plub.plubserver.domain.feed.model.Feed;
import org.springframework.data.domain.Pageable;
import plub.plubserver.domain.feed.model.FeedComment;


public interface FeedCommentRepositoryCustom {
    Page<FeedComment> findAllByFeed(Feed feed, Pageable pageable, Long cursorId);
}