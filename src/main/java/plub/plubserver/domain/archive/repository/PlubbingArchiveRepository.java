package plub.plubserver.domain.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.archive.model.PlubbingArchive;

public interface PlubbingArchiveRepository extends JpaRepository<PlubbingArchive, Long> {
}
