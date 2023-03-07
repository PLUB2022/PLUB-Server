package plub.plubserver.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedComment;

import java.util.Optional;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long>, FeedCommentRepositoryCustom {
    Long countAllByVisibilityAndFeed(boolean visibility, Feed feed);

    Optional<FeedComment> findFirstByVisibilityAndFeedId(boolean visibility, Long feedId);
}

