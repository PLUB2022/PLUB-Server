package plub.plubserver.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedComment;

import java.util.List;

public interface FeedCommentRepository extends JpaRepository<FeedComment, Long> {
    List<FeedComment> findAllByFeedAndVisibility(Feed feed, Boolean visibility);
}

