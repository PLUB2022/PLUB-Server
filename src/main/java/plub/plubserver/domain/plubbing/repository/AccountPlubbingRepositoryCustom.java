package plub.plubserver.domain.plubbing.repository;

import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.PlubbingStatus;

import java.util.List;

public interface AccountPlubbingRepositoryCustom {
    List<AccountPlubbing> findAllByAccount(
            Account currentAccount,
            PlubbingStatus plubbingStatus
    );

}
