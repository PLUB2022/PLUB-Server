package plub.plubserver.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.SuspendAccount;

import java.util.Optional;

public interface SuspendAccountRepository extends JpaRepository<SuspendAccount, Long> {
    Optional<SuspendAccount> findByAccountIdAndCheckSuspendedIsTrue(Long accountId);
    Optional<SuspendAccount> findByAccountEmailAndCheckSuspendedIsTrue(String email);
}
