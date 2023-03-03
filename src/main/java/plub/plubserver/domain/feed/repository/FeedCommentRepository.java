package plub.plubserver.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedComment;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long>, FeedCommentRepositoryCustom {
    Long countAllByFeed(Feed feed);
}

