package plub.plubserver.domain.plubbing.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.AccountPlubbing;
import plub.plubserver.domain.plubbing.model.PlubbingStatus;

import static plub.plubserver.domain.plubbing.model.QAccountPlubbing.accountPlubbing;

@RequiredArgsConstructor
public class AccountPlubbingRepositoryImpl implements AccountPlubbingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AccountPlubbing> findAllByAccount(
            Account currentAccount,
            PlubbingStatus plubbingStatus,
            Pageable pageable,
            Long cursorId
    ) {
        JPAQuery<AccountPlubbing> query = queryFactory
                .selectFrom(accountPlubbing)
                .where(
                        accountPlubbing.account.eq(currentAccount),
                        accountPlubbing.plubbing.status.eq(plubbingStatus),
                        cursorId(cursorId)
                )
                .orderBy(accountPlubbing.id.desc());

        return PageableExecutionUtils.getPage(
                query.orderBy(accountPlubbing.id.desc())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(accountPlubbing)
                        .fetch().size()
        );
    }

    public BooleanExpression cursorId(Long cursorId) {
        return cursorId == null ? null : accountPlubbing.id.lt(cursorId);
    }
}
