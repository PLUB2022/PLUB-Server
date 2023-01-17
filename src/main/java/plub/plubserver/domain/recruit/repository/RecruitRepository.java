package plub.plubserver.domain.recruit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.recruit.model.Recruit;

public interface RecruitRepository extends JpaRepository<Recruit, Long>, RecruitRepositoryCustom {
}
