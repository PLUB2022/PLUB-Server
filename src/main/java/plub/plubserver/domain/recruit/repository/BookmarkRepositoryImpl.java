package plub.plubserver.domain.recruit.repository;

import org.springframework.data.domain.Page;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.recruit.model.Bookmark;
import plub.plubserver.util.CursorUtils;
import org.springframework.data.domain.Pageable;

import static plub.plubserver.domain.recruit.model.QBookmark.bookmark;

@Slf4j
@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Bookmark> findAllByAccountId(Long accountId, Long cursorId, Pageable pageable) {
        JPQLQuery<Bookmark> query = queryFactory
                .selectFrom(bookmark)
                .where(bookmark.account.id.eq(accountId),
                        getCursorId(cursorId))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(bookmark.id.asc())
                        .limit(CursorUtils.TEN_AMOUNT)
                        .fetch(),
                pageable,
                () -> countAllByAccountId(accountId));
    }

    private BooleanExpression getCursorId(Long cursorId) {
        if (cursorId == null || cursorId == 0) return null;
        else return bookmark.id.gt(cursorId);
    }

    @Override
    public Long countAllByAccountId(Long accountId) {
        return (long) queryFactory.selectFrom(bookmark)
                .where(bookmark.account.id.eq(accountId))
                .fetch().size();
    }
}
