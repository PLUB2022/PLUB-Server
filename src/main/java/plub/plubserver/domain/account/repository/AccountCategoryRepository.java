package plub.plubserver.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.AccountCategory;

public interface AccountCategoryRepository extends JpaRepository<AccountCategory, Long> {
}
