package plub.plubserver.domain.calendar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.domain.calendar.model.Calendar;

public interface CalendarRepositoryCustom {
    Page<Calendar> findAllByPlubbingId(
            Long plubbingId,
            Pageable pageable,
            Long cursorId
    );
}
