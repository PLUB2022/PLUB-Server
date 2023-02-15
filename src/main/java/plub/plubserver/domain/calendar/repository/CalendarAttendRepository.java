package plub.plubserver.domain.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.calendar.model.CalendarAttend;

import java.util.List;
import java.util.Optional;

public interface CalendarAttendRepository extends JpaRepository<CalendarAttend, Long> {
    Optional<CalendarAttend> findByCalendarIdAndAccountId(Long calendarId, Long accountId);
    List<CalendarAttend> findByCalendarIdOrderByAttendStatus(Long calendarId);
}
