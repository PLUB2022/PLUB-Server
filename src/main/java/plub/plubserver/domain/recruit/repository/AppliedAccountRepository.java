package plub.plubserver.domain.recruit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.recruit.model.AppliedAccount;
import plub.plubserver.domain.recruit.model.Recruit;

import java.util.List;
import java.util.Optional;

public interface AppliedAccountRepository extends JpaRepository<AppliedAccount, Long> {
    Boolean existsByAccountAndRecruit(Account account, Recruit recruit);
    Optional<AppliedAccount> findByAccountIdAndRecruitId(Long accountId, Long recruitId);

    // for test
    List<AppliedAccount> findAllByRecruitId(Long recruitId);

    Optional<AppliedAccount> findByAccountAndRecruit(Account account, Recruit recruit);
}
