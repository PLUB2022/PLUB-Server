package plub.plubserver.domain.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.archive.model.Archive;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {
}
