package plub.plubserver.domain.account.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.domain.account.model.Account;

public interface AccountRepositoryCustom {
    Page<Account> findBySearch(
            String startedAt,
            String endedAt,
            String keyword,
            Pageable pageable
    );
}
