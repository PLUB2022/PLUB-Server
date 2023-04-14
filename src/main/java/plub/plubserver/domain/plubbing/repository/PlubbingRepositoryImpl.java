package plub.plubserver.domain.plubbing.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubbing.model.*;
import plub.plubserver.domain.recruit.model.RecruitStatus;

import java.util.List;

import static plub.plubserver.domain.account.model.QAccount.account;
import static plub.plubserver.domain.category.model.QPlubbingSubCategory.plubbingSubCategory;
import static plub.plubserver.domain.category.model.QSubCategory.subCategory;
import static plub.plubserver.domain.plubbing.model.QAccountPlubbing.accountPlubbing;
import static plub.plubserver.domain.plubbing.model.QPlubbing.plubbing;
import static plub.plubserver.domain.plubbing.model.QPlubbingMeetingDay.plubbingMeetingDay;

@RequiredArgsConstructor
public class PlubbingRepositoryImpl implements PlubbingRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Plubbing> findAllBySubCategory(List<Long> subCategoryId, Pageable pageable, Long cursorId) {
        JPQLQuery<Plubbing> query = queryFactory
                .selectFrom(plubbing)
                .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                .join(plubbingSubCategory.subCategory, subCategory)
                .where(inSubCategoryId(subCategoryId),
                        plubbing.status.eq(PlubbingStatus.ACTIVE),
                        plubbing.visibility.eq(true),
                        plubbing.recruit.status.eq(RecruitStatus.RECRUITING),
                        getCursorId(cursorId))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                query::fetchCount);
    }

    @Override
    public Page<Plubbing> findAllByViews(Pageable pageable, Long cursorId, Integer views) {
        JPQLQuery<Plubbing> query = queryFactory
                .selectFrom(plubbing)
                .where(plubbing.status.eq(PlubbingStatus.ACTIVE),
                        plubbing.visibility.eq(true),
                        plubbing.recruit.status.eq(RecruitStatus.RECRUITING),
                        getCursorIdByViews(cursorId, views))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(plubbing.views.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                query::fetchCount);
    }

    @Override
    public Page<Plubbing> findAllByCategory(Long categoryId, Pageable pageable, SortType sortType, Long cursorId) {
        JPQLQuery<Plubbing> query = queryFactory
                .selectFrom(plubbing)
                .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                .join(plubbingSubCategory.subCategory, subCategory)
                .where(subCategory.category.id.eq(categoryId),
                        plubbing.status.eq(PlubbingStatus.ACTIVE),
                        plubbing.visibility.eq(true),
                        plubbing.recruit.status.eq(RecruitStatus.RECRUITING),
                        getCursorId(cursorId))
                .distinct();

        OrderSpecifier<?> order;
        if (sortType == SortType.POPULAR) {
            order = plubbing.views.desc();
        } else {
            order = plubbing.modifiedAt.desc();
        }

        return PageableExecutionUtils.getPage(
                query.orderBy(order)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                query::fetchCount);
    }

    @Override
    public Page<Plubbing> findAllByCategoryAndFilter(Long categoryId, List<Long> subCategoryId, List<MeetingDay> meetingDays, Integer accountNum, Pageable pageable, SortType sortType, Long cursorId) {

        OrderSpecifier<?> order;
        if (sortType == SortType.POPULAR) {
            order = plubbing.views.desc();
        } else {
            order = plubbing.modifiedAt.desc();
        }

        JPQLQuery<Plubbing> query = queryFactory
                .selectFrom(plubbing)
                .join(plubbing.days, plubbingMeetingDay)
                .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                .join(plubbingSubCategory.subCategory, subCategory)
                .where(inSubCategoryId(subCategoryId),
                        eqAccountNum(accountNum),
                        inDays(meetingDays),
                        subCategory.category.id.eq(categoryId),
                        plubbing.status.eq(PlubbingStatus.ACTIVE),
                        plubbing.visibility.eq(true),
                        getCursorId(cursorId))
                .distinct();

        return PageableExecutionUtils.getPage(
                query.orderBy(order)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                query::fetchCount);
    }

    // 내가 호스트인 모임 전체 조회
    @Override
    public List<Plubbing> findAllByHost(Account host) {
        return queryFactory
                .selectFrom(plubbing)
                .join(plubbing.accountPlubbingList, accountPlubbing)
                .fetchJoin()
                .join(accountPlubbing.account, account)
                .fetchJoin()
                .where(account.id.eq(host.getId()))
                .distinct()
                .fetch();
    }

    private BooleanExpression eqAccountNum(Integer accountNum) {
        return accountNum != null ? plubbing.curAccountNum.eq(accountNum) : null;
    }

    private BooleanExpression inDays(List<MeetingDay> days) {
        return !days.isEmpty() ? plubbingMeetingDay.day.in(days) : null;
    }

    private BooleanExpression inSubCategoryId(List<Long> subCategoryId) {
        return subCategoryId != null ? subCategory.id.in(subCategoryId) : null;
    }

    private BooleanExpression getCursorIdByViews(Long cursorId, Integer views) {
         return (cursorId == null || views == null) ? null : plubbing.views.loe(views)
                 .and(plubbing.id.gt(cursorId)
                 .or(plubbing.views.lt(views)));
    }

    private BooleanExpression getCursorId(Long cursorId) {
        return (cursorId == null || cursorId == 0) ? null : plubbing.id.gt(cursorId);
    }
}