package plub.plubserver.domain.feed.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.feed.model.Feed;
import plub.plubserver.domain.feed.model.ViewType;
import plub.plubserver.domain.plubbing.model.Plubbing;

public interface FeedRepositoryCustom {
    Page<Feed> findAllByPlubbingAndPinAndVisibilityCursor(
            Plubbing plubbing,
            Boolean pin,
            Boolean visibility,
            Pageable pageable,
            Long cursorId
    );

    Page<Feed> findAllByPlubbingAndAccountAndVisibilityAndViewType(
            Plubbing plubbing,
            Account account,
            Boolean visibility,
            ViewType viewType,
            Pageable pageable,
            Long cursorId
    );

}
