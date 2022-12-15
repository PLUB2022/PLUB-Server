package plub.plubserver.domain.plubbing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.AccountPlubbingStatus;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.util.List;
import java.util.Optional;

public interface AccountPlubbingRepository extends JpaRepository<AccountPlubbing, Long> {
    Optional<List<AccountPlubbing>> findAllByAccountAndIsHostAndAccountPlubbingStatus(Account currentAccount, Boolean isHost, AccountPlubbingStatus status);

    Optional<List<AccountPlubbing>> findAllByPlubbingId(Long plubbingId);

    boolean existsByAccountAndPlubbingId(Account currentAccount, Long plubbingId);

    Optional<AccountPlubbing> findByAccountAndPlubbing(Account currentAccount, Plubbing plubbing);

}


