package plub.plubserver.domain.feed.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.feed.model.PlubbingFeed;

public interface PlubbingFeedRepository extends JpaRepository<PlubbingFeed, Long> {
}

