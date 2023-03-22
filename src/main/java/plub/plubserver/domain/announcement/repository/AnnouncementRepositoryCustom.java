package plub.plubserver.domain.announcement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.domain.announcement.model.Announcement;

public interface AnnouncementRepositoryCustom {
    Page<Announcement> findAllByOrderByCreatedAtDesc(
            Pageable pageable,
            Long cursorId,
            String createdAt
    );
}
