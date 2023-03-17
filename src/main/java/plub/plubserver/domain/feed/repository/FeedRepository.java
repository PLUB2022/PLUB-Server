package plub.plubserver.domain.feed.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, FeedRepositoryCustom {

    List<Feed> findAllByPlubbingAndPinAndVisibility(Plubbing plubbing, Boolean pin, Boolean visibility, Sort sort);

    Long countByPin(boolean pin);

    @Query("select distinct count(f) from Feed f where f.visibility = true")
    Long countAll();

    Long countAllByPlubbingAndVisibility(Plubbing plubbing, boolean visibility);
}
