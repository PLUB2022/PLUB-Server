package plub.plubserver.domain.recruit.repository;

import org.springframework.data.domain.Page;
import plub.plubserver.domain.recruit.model.Bookmark;
import org.springframework.data.domain.Pageable;

public interface BookmarkRepositoryCustom {
    Page<Bookmark> findAllByAccountId(Long accountId, Long cursorId, Pageable pageable);

    Long countAllByAccountId(Long accountId);
}
