package plub.plubserver.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.notice.model.PlubbingNotice;

public interface PlubbingNoticeRepository extends JpaRepository<PlubbingNotice, Long> {
}

