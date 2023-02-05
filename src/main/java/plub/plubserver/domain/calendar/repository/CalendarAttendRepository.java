package plub.plubserver.domain.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.calendar.model.CalendarAttend;

public interface CalendarAttendRepository extends JpaRepository<CalendarAttend, Long> {

}
