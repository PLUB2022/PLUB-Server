package plub.plubserver.domain.announcement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.announcement.model.Announcement;

import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long>, AnnouncementRepositoryCustom {
    Optional<Announcement> findFirstByVisibilityIsTrueOrderByCreatedAtDesc();
}
