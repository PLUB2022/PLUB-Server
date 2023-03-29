package plub.plubserver.domain.recruit.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.recruit.model.RecruitSearchType;
import plub.plubserver.util.CursorUtils;

import java.util.List;

import static plub.plubserver.domain.account.model.QAccount.account;
import static plub.plubserver.domain.category.model.QPlubbingSubCategory.plubbingSubCategory;
import static plub.plubserver.domain.category.model.QSubCategory.subCategory;
import static plub.plubserver.domain.plubbing.model.QPlubbing.plubbing;
import static plub.plubserver.domain.recruit.model.QBookmark.bookmark;
import static plub.plubserver.domain.recruit.model.QRecruit.recruit;

@Slf4j
@RequiredArgsConstructor
public class RecruitRepositoryImpl implements RecruitRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private JPQLQuery<Recruit> getSearchDefaultQuery(RecruitSearchType type, String keyword) {
        JPQLQuery<Recruit> prefixQuery = queryFactory.selectFrom(recruit)
                .leftJoin(recruit.plubbing, plubbing)
                .fetchJoin()
                .leftJoin(plubbing.plubbingSubCategories, plubbingSubCategory)
                .leftJoin(plubbingSubCategory.subCategory, subCategory)
                .distinct();

        return switch (type) {
            case TITLE -> prefixQuery.where(recruit.title.contains(keyword));
            case NAME -> prefixQuery.where(plubbing.name.contains(keyword));
            // MIX = TITLE_INTRO
            default -> prefixQuery.where(recruit.title.contains(keyword)
                    .or(recruit.introduce.contains(keyword)));
        };
    }

    @Override
    public Page<Recruit> search(
            Long cursorId,
            Pageable pageable,
            SortType sortType,
            RecruitSearchType type,
            String keyword
    ) {
        JPQLQuery<Recruit> searchQuery = getSearchDefaultQuery(type, keyword);
        searchQuery = searchQuery.where(getCursorId(cursorId));
        OrderSpecifier<?> order; // types : String, Integer
        if (sortType == SortType.POPULAR) order = recruit.views.desc();
        else order = recruit.modifiedAt.desc();
        return PageableExecutionUtils.getPage(
                searchQuery.orderBy(order).limit(CursorUtils.TEN_AMOUNT).fetch(),
                pageable,
                searchQuery::fetchCount
        );
    }

    private BooleanExpression getCursorId(Long cursorId) {
        if (cursorId == null || cursorId == 0) return null;
        else return recruit.id.gt(cursorId);
    }

    @Override
    public Long countAllBySearch(RecruitSearchType type, String keyword) {
        return getSearchDefaultQuery(type, keyword).fetchCount();
    }


    @Override
    public List<Long> findAllBookmarkedRecruitIdByAccountId(Long accountId) {
        return queryFactory.selectFrom(bookmark)
                .leftJoin(bookmark.account, account)
                .fetchJoin()
                .where(bookmark.account.id.eq(accountId))
                .fetch()
                .stream()
                .map(it -> it.getRecruit().getId())
                .toList();
    }

    @Override
    public List<Recruit> findAllPlubbingRecruitByAccountId(Long accountId) {
        return queryFactory.selectFrom(recruit)
                .leftJoin(recruit.plubbing, plubbing)
                .fetchJoin()
                .where(recruit.plubbing.accountPlubbingList.any().account.id.eq(accountId))
                .fetch();
    }
}
