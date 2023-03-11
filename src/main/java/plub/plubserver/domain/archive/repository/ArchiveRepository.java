package plub.plubserver.domain.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.archive.model.Archive;

import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long>, ArchiveRepositoryCustom {
    Optional<Archive> findFirstByPlubbingIdOrderBySequenceDesc(Long plubbingId);
    Long countAllByPlubbingId(Long plubbingId);
}
