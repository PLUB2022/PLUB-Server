package plub.plubserver.domain.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.calendar.model.PlubbingCalendar;

public interface PlubbingCalendarRepository extends JpaRepository<PlubbingCalendar, Long> {
}
