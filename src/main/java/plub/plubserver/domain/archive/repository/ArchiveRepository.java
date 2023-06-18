package plub.plubserver.domain.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.archive.model.Archive;

import java.util.List;
import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long>, ArchiveRepositoryCustom {
    Optional<Archive> findFirstByPlubbingIdOrderBySequenceDesc(Long plubbingId);
    Long countAllByPlubbingId(Long plubbingId);

    List<Archive> findAllByAccount(Account account);
}
