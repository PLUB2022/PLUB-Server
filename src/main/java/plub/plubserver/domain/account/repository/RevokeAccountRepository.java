package plub.plubserver.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.RevokeAccount;

public interface RevokeAccountRepository extends JpaRepository<RevokeAccount, Long> {
}
