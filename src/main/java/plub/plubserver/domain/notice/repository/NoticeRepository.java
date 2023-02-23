package plub.plubserver.domain.notice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.plubbing.model.Plubbing;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAllByPlubbingAndVisibility(Plubbing plubbing, boolean visibility,Pageable pageable);
}

