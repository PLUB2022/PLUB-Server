package plub.plubserver.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.AccountNicknameHistory;

public interface AccountNicknameHistoryRepository extends JpaRepository<AccountNicknameHistory, Long> {
    int countAllByAccount(Account account);
}
