package plub.plubserver.domain.plubbing.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
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
                        eqPlubbingStatus(plubbingStatus)
                )
                .orderBy(accountPlubbing.id.desc());
        return query.fetch();
    }

    // plubbingStatus가 END 일때 EXIT 랑 END랑 합치기
    public BooleanExpression eqPlubbingStatus(AccountPlubbingStatus plubbingStatus) {
        if (plubbingStatus.equals(AccountPlubbingStatus.END)) {
            return accountPlubbing.accountPlubbingStatus.eq(AccountPlubbingStatus.EXIT)
                    .or(accountPlubbing.accountPlubbingStatus.eq(AccountPlubbingStatus.END));
        } else {
            return accountPlubbing.accountPlubbingStatus.eq(plubbingStatus);
        }
    }
}
