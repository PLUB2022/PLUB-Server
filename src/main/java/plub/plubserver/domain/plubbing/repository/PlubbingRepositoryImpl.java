package plub.plubserver.domain.plubbing.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.plubbing.model.*;
import plub.plubserver.domain.recruit.model.RecruitStatus;

import java.util.List;

import static plub.plubserver.domain.category.model.QPlubbingSubCategory.plubbingSubCategory;
import static plub.plubserver.domain.category.model.QSubCategory.subCategory;
import static plub.plubserver.domain.plubbing.model.QPlubbing.plubbing;
import static plub.plubserver.domain.plubbing.model.QPlubbingMeetingDay.plubbingMeetingDay;

@RequiredArgsConstructor
public class PlubbingRepositoryImpl implements PlubbingRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Plubbing> findAllBySubCategory(List<SubCategory> subCategories, Pageable pageable) {
       List<Plubbing> result = queryFactory
               .selectFrom(plubbing)
               .join(plubbing.plubbingSubCategories, plubbingSubCategory)
               .join(plubbingSubCategory.subCategory, subCategory)
               .where(subCategory.in(subCategories),
                       plubbing.status.eq(PlubbingStatus.ACTIVE),
                       plubbing.visibility.eq(true),
                       plubbing.recruit.status.eq(RecruitStatus.RECRUITING))
               .offset(pageable.getOffset())
               .limit(pageable.getPageSize())
               .distinct()
               .fetch();

        return PageableExecutionUtils.getPage(
                result,
                pageable,
                result::size
        );
    }

    @Override
    public Page<Plubbing> findAllByViews(Pageable pageable) {
        List<Plubbing> result = queryFactory
                .selectFrom(plubbing)
                .where(plubbing.status.eq(PlubbingStatus.ACTIVE),
                        plubbing.visibility.eq(true),
                        plubbing.recruit.status.eq(RecruitStatus.RECRUITING))
                .orderBy(plubbing.views.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        return PageableExecutionUtils.getPage(
                result,
                pageable,
                result::size
        );
    }

    @Override
    public Page<Plubbing> findAllByCategoryId(Long categoryId, Pageable pageable, SortType sortType) {

        OrderSpecifier<?> order;
        if (sortType == SortType.POPULAR) {
            order = plubbing.views.desc();
        } else {
            order = plubbing.modifiedAt.desc();
        }

        List<Plubbing> result = queryFactory
                .selectFrom(plubbing)
                .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                .join(plubbingSubCategory.subCategory, subCategory)
                .where(subCategory.category.id.eq(categoryId),
                        plubbing.status.eq(PlubbingStatus.ACTIVE),
                        plubbing.visibility.eq(true),
                        plubbing.recruit.status.eq(RecruitStatus.RECRUITING))
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        return PageableExecutionUtils.getPage(
                result,
                pageable,
                result::size
        );
    }

    @Override
    public Page<Plubbing> findAllByCategoryIdAndSubCategoryIdAndDaysAndAccountNum(Long categoryId, List<Long> subCategoryId, List<MeetingDay> meetingDays, Integer accountNum, Pageable pageable, SortType sortType) {

        OrderSpecifier<?> order;
        if (sortType == SortType.POPULAR) {
            order = plubbing.views.desc();
        } else {
            order = plubbing.modifiedAt.desc();
        }

        List<Plubbing> result = queryFactory
                .selectFrom(plubbing)
                .join(plubbing.days, plubbingMeetingDay)
                .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                .join(plubbingSubCategory.subCategory, subCategory)
                .where(inSubCategoryId(subCategoryId),
                        eqAccountNum(accountNum),
                        inDays(meetingDays),
                        subCategory.category.id.eq(categoryId),
                        plubbing.status.eq(PlubbingStatus.ACTIVE),
                        plubbing.visibility.eq(true))
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        return PageableExecutionUtils.getPage(
                result,
                pageable,
                result::size
        );
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
}