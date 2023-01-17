package plub.plubserver.domain.recruit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.recruit.model.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
