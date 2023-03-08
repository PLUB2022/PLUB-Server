package plub.plubserver.domain.calendar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import plub.plubserver.domain.calendar.model.Calendar;

import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long>, CalendarRepositoryCustom {
    @Query("select distinct count(c) from Calendar c where c.plubbing.id = :plubbing and c.visibility = true")
    Long countAllByPlubbing(@Param("plubbing") Long plubbing);

    Optional<Calendar> findByIdAndPlubbingIdAndVisibilityIsTrue(Long id, Long plubbingId);

    Optional<Calendar> findFirstByPlubbingIdAndVisibilityIsTrueOrderByStartedAtDesc(Long plubbingId);
}
