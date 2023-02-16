package plub.plubserver.domain.plubbing.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.plubbing.model.*;

import java.util.ArrayList;
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
        return PageableExecutionUtils.getPage(
                queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, subCategory)
                        .where(subCategory.in(subCategories),
                                plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, subCategory)
                        .where(subCategory.in(subCategories),
                                plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .fetch().size()
        );
    }

    @Override
    public Page<Plubbing> findAllByViews(Pageable pageable) {
        return PageableExecutionUtils.getPage(
                queryFactory
                        .selectFrom(plubbing)
                        .where(plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .orderBy(plubbing.views.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(plubbing)
                        .where(plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .fetch().size()
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

        return PageableExecutionUtils.getPage(
                queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, subCategory)
                        .where(subCategory.category.id.eq(categoryId),
                                plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .orderBy(order)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, subCategory)
                        .where(subCategory.category.id.eq(categoryId))
                        .fetch().size()
        );
    }

    @Override
    public Page<Plubbing> findAllByCategoryIdAndAccountNum(Long categoryId, Integer accountNum, Pageable pageable, SortType sortType) {

        OrderSpecifier<?> order;
        if (sortType == SortType.POPULAR) {
            order = plubbing.views.desc();
        } else {
            order = plubbing.modifiedAt.desc();
        }

        return PageableExecutionUtils.getPage(
                queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, subCategory)
                        .where(subCategory.category.id.eq(categoryId),
                                plubbing.curAccountNum.eq(accountNum),
                                plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .orderBy(order)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, subCategory)
                        .where(subCategory.category.id.eq(categoryId),
                                plubbing.curAccountNum.eq(accountNum),
                                plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .fetch().size()
        );
    }

    @Override
    public Page<Plubbing> findAllByCategoryIdAndDays(Long categoryId, List<String> days, Pageable pageable, SortType sortType) {

        OrderSpecifier<?> order;
        if (sortType == SortType.POPULAR) {
            order = plubbing.views.desc();
        } else {
            order = plubbing.modifiedAt.desc();
        }

        List<Plubbing> plubbings = new ArrayList<>();
        for (String day : days) {
            plubbings.addAll(queryFactory
                    .selectFrom(plubbing)
                    .join(plubbing.days, plubbingMeetingDay)
                    .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                    .join(plubbingSubCategory.subCategory, subCategory)
                    .where(subCategory.category.id.eq(categoryId),
                            plubbingMeetingDay.day.eq(MeetingDay.valueOf(day)),
                            plubbing.status.eq(PlubbingStatus.ACTIVE),
                            plubbing.visibility.eq(true))
                    .orderBy(order)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch());
        }

        return PageableExecutionUtils.getPage(
                plubbings,
                pageable,
                plubbings::size
        );
    }

    @Override
    public Page<Plubbing> findAllByCategoryIdAndSubCategoryId(Long categoryId, List<Long> subCategoryId, Pageable pageable, SortType sortType) {

        OrderSpecifier<?> order;
        if (sortType == SortType.POPULAR) {
            order = plubbing.views.desc();
        } else {
            order = plubbing.modifiedAt.desc();
        }

        return PageableExecutionUtils.getPage(
                queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, subCategory)
                        .where(subCategory.category.id.eq(categoryId),
                                subCategory.id.in(subCategoryId),
                                plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .orderBy(order)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, subCategory)
                        .where(subCategory.category.id.eq(categoryId),
                                subCategory.id.in(subCategoryId),
                                plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .fetch().size()
        );
    }

    @Override
    public Page<Plubbing> findAllByCategoryIdAndDaysAndAccountNum(Long categoryId, List<String> days, Integer accountNum, Pageable pageable, SortType sortType) {

        OrderSpecifier<?> order;
        if (sortType == SortType.POPULAR) {
            order = plubbing.views.desc();
        } else {
            order = plubbing.modifiedAt.desc();
        }

        List<Plubbing> plubbings = new ArrayList<>();
        for (String day : days) {
            plubbings.addAll(queryFactory
                    .selectFrom(plubbing)
                    .join(plubbing.days, plubbingMeetingDay)
                    .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                    .join(plubbingSubCategory.subCategory, subCategory)
                    .where(subCategory.category.id.eq(categoryId),
                            plubbingMeetingDay.day.eq(MeetingDay.valueOf(day)),
                            plubbing.curAccountNum.eq(accountNum),
                            plubbing.status.eq(PlubbingStatus.ACTIVE),
                            plubbing.visibility.eq(true))
                    .orderBy(order)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch());
        }

        return PageableExecutionUtils.getPage(
                plubbings,
                pageable,
                plubbings::size
        );
    }

    @Override
    public Page<Plubbing> findAllByCategoryIdAndSubCategoryIdAndAccountNum(Long categoryId, List<Long> subCategoryId, Integer accountNum, Pageable pageable, SortType sortType) {

        OrderSpecifier<?> order;
        if (sortType == SortType.POPULAR) {
            order = plubbing.views.desc();
        } else {
            order = plubbing.modifiedAt.desc();
        }

        return PageableExecutionUtils.getPage(
                queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, subCategory)
                        .where(subCategory.category.id.eq(categoryId),
                                subCategory.id.in(subCategoryId),
                                plubbing.curAccountNum.eq(accountNum),
                                plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .orderBy(order)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, subCategory)
                        .where(subCategory.category.id.eq(categoryId),
                                subCategory.id.in(subCategoryId),
                                plubbing.curAccountNum.eq(accountNum),
                                plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .fetch().size()
        );
    }

    @Override
    public Page<Plubbing> findAllByCategoryIdAndSubCategoryIdAndDays(Long categoryId, List<Long> subCategoryId, List<String> days, Pageable pageable, SortType sortType) {

        OrderSpecifier<?> order;
        if (sortType == SortType.POPULAR) {
            order = plubbing.views.desc();
        } else {
            order = plubbing.modifiedAt.desc();
        }

        List<Plubbing> plubbings = new ArrayList<>();
        for (String day : days) {
            plubbings.addAll(queryFactory
                    .selectFrom(plubbing)
                    .join(plubbing.days, plubbingMeetingDay)
                    .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                    .join(plubbingSubCategory.subCategory, subCategory)
                    .where(subCategory.category.id.eq(categoryId),
                            subCategory.id.in(subCategoryId),
                            plubbingMeetingDay.day.eq(MeetingDay.valueOf(day)),
                            plubbing.status.eq(PlubbingStatus.ACTIVE),
                            plubbing.visibility.eq(true))
                    .orderBy(order)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch());
        }

        return PageableExecutionUtils.getPage(
                plubbings,
                pageable,
                plubbings::size
        );
    }

    @Override
    public Page<Plubbing> findAllByCategoryIdAndSubCategoryIdAndDaysAndAccountNum(Long categoryId, List<Long> subCategoryId, List<String> days, Integer accountNum, Pageable pageable, SortType sortType) {

        OrderSpecifier<?> order;
        if (sortType == SortType.POPULAR) {
            order = plubbing.views.desc();
        } else {
            order = plubbing.modifiedAt.desc();
        }

        List<Plubbing> plubbings = new ArrayList<>();
        for (String day : days) {
            plubbings.addAll(queryFactory
                    .selectFrom(plubbing)
                    .join(plubbing.days, plubbingMeetingDay)
                    .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                    .join(plubbingSubCategory.subCategory, subCategory)
                    .where(subCategory.category.id.eq(categoryId),
                            subCategory.id.in(subCategoryId),
                            plubbingMeetingDay.day.eq(MeetingDay.valueOf(day)),
                            plubbing.curAccountNum.eq(accountNum),
                            plubbing.status.eq(PlubbingStatus.ACTIVE),
                            plubbing.visibility.eq(true))
                    .orderBy(order)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch());
        }

        return PageableExecutionUtils.getPage(
                plubbings,
                pageable,
                plubbings::size
        );
    }
}