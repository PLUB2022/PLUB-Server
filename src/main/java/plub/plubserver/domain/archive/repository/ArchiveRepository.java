package plub.plubserver.domain.archive.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.archive.model.Archive;

import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    Page<Archive> findAllByPlubbingIdOrderBySequenceDesc(Long plubbingId, Pageable pageable);

    Optional<Archive> findFirstByPlubbingIdOrderBySequenceDesc(Long plubbingId);
}
