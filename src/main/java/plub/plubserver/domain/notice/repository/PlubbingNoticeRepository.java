package plub.plubserver.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.notice.model.PlubbingNotice;

public interface PlubbingNoticeRepository extends JpaRepository<PlubbingNotice, Long> {
}

