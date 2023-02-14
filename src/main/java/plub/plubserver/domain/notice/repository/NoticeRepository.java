package plub.plubserver.domain.notice.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByPlubbingAndVisibility(Plubbing plubbing, boolean visibility, Sort sort);
}

