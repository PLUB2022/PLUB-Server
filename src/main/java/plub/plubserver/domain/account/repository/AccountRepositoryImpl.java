package plub.plubserver.domain.account.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.account.model.Account;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static plub.plubserver.domain.account.model.QAccount.account;

@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Account> findBySearch(String startedAt, String endedAt, String keyword, Pageable pageable) {
        JPQLQuery<Account> query = queryFactory.selectFrom(account)
                .where(
                        containSearch(keyword),
                        betweenDate(startedAt, endedAt)
                )
                .orderBy(account.id.desc());

        return PageableExecutionUtils.getPage(
                query.offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                query::fetchCount);
    }

    private BooleanExpression containSearch(String search) {
        if(search == null || search.isEmpty()) {
            return null;
        }
        return account.email.contains(search).or(account.nickname.contains(search));
    }

    private BooleanExpression betweenDate(String startedAt, String endedAt) {
        if(startedAt == null || startedAt.isEmpty()) {
            return null;
        }
        if(endedAt == null || endedAt.isEmpty()) {
            return null;
        }

        startedAt = startedAt + " 00:00:00";
        endedAt = endedAt + " 23:59:59";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(startedAt, formatter);
        LocalDateTime endDate = LocalDateTime.parse(endedAt, formatter);
        return account.joinDate.between(startDate, endDate);
    }

}
