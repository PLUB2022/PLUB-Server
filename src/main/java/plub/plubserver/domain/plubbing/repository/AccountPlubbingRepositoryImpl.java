package plub.plubserver.domain.plubbing.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.AccountPlubbingStatus;

import java.util.List;

import static plub.plubserver.domain.plubbing.model.QAccountPlubbing.accountPlubbing;

@RequiredArgsConstructor
public class AccountPlubbingRepositoryImpl implements AccountPlubbingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AccountPlubbing> findAllByAccount(
            Account currentAccount,
            AccountPlubbingStatus plubbingStatus
    ) {
        JPAQuery<AccountPlubbing> query = queryFactory
                .selectFrom(accountPlubbing)
                .where(
                        accountPlubbing.account.eq(currentAccount),
                        accountPlubbing.accountPlubbingStatus.eq(plubbingStatus)
                )
                .orderBy(accountPlubbing.id.desc());

        return query.fetch();
    }
}
