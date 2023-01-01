package plub.plubserver.domain.plubbing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.plubbing.model.Plubbing;

public interface PlubbingRepository extends JpaRepository<Plubbing, Long>, PlubbingRepositoryCustom {
}

