package plub.plubserver.domain.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.calendar.model.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
}
