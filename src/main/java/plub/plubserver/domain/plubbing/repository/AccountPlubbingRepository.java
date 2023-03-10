package plub.plubserver.domain.plubbing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.AccountPlubbingStatus;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.util.List;
import java.util.Optional;

public interface AccountPlubbingRepository extends JpaRepository<AccountPlubbing, Long>, AccountPlubbingRepositoryCustom {
    List<AccountPlubbing> findAllByAccountAndIsHostAndAccountPlubbingStatus(Account currentAccount, Boolean isHost, AccountPlubbingStatus status);

    List<AccountPlubbing> findAllByPlubbingId(Long plubbingId);

    boolean existsByAccountAndPlubbingId(Account currentAccount, Long plubbingId);

    Optional<AccountPlubbing> findByAccountAndPlubbing(Account currentAccount, Plubbing plubbing);

    @Query("select ap from AccountPlubbing ap where ap.plubbing.id = :plubbingId and ap.isHost = true")
    Optional<AccountPlubbing> findByPlubbingIdAndIsHost(@Param("plubbingId") Long plubbingId);
    void deleteByPlubbingAndAccount(Plubbing plubbing, Account account);

}


