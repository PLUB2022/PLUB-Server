package plub.plubserver.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.AccountPolicy;

public interface AccountPolicyRepository extends JpaRepository<AccountPolicy, Long> {
}
