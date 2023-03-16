package plub.plubserver.domain.notice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.plubbing.model.Plubbing;

public interface NoticeRepositoryCustom {
    Page<Notice> findAllByPlubbingAndVisibilityCursor(
            Plubbing plubbing,
            boolean visibility,
            Pageable pageable,
            Long cursorId
    );
}

