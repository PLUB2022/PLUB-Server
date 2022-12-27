package plub.plubserver.domain.recruit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import plub.plubserver.domain.recruit.model.Recruit;

import java.util.Optional;

public interface RecruitRepository extends JpaRepository<Recruit, Long> {

    @Query("SELECT r FROM Recruit r WHERE r.id = :id AND r.visibility = true")
    Optional<Recruit> findById(@Param("id") Long recruitId);
}
