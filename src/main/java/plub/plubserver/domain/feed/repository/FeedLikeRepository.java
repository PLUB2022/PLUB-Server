package plub.plubserver.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.FeedLike;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {
    boolean existsByAccountAndFeed(Account account, Feed feed);
}