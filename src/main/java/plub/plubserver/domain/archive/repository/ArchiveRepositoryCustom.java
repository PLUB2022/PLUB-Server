package plub.plubserver.domain.archive.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.domain.archive.model.Archive;

public interface ArchiveRepositoryCustom {
    Page<Archive> findAllByPlubbingId(Long plubbingId, Pageable pageable, Long cursorId);
}
