package plub.plubserver.domain.calendar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import plub.plubserver.domain.calendar.model.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    @Query("select c from Calendar c order by c.staredAt desc")
    Page<Calendar> findAllByPlubbingId(Long plubbingId, Pageable pageable);
}
