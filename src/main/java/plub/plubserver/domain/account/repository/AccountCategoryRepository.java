package plub.plubserver.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.AccountCategory;

import java.util.List;


public interface AccountCategoryRepository extends JpaRepository<AccountCategory, Long> {
    List<AccountCategory> findAllByAccount(Account myAccount);
}

