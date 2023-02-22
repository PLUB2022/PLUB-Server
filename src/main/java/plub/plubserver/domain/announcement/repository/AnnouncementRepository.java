package plub.plubserver.domain.announcement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.announcement.model.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    Page<Announcement> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
