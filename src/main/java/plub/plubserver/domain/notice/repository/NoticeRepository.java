package plub.plubserver.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.notice.model.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}

