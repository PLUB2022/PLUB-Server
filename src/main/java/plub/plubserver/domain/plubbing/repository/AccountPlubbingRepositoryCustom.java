package plub.plubserver.domain.plubbing.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.PlubbingStatus;

public interface AccountPlubbingRepositoryCustom {
    Page<AccountPlubbing> findAllByAccount(
            Account currentAccount,
            PlubbingStatus plubbingStatus,
            Pageable pageable,
            Long cursorId
    );

}
