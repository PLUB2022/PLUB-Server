package plub.plubserver.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.SuspendAccount;

import java.util.Optional;

public interface SuspendAccountRepository extends JpaRepository<SuspendAccount, Long> {
    Optional<SuspendAccount> findByAccountId(Long accountId);
    Optional<SuspendAccount> findByAccountEmail(String email);
}
