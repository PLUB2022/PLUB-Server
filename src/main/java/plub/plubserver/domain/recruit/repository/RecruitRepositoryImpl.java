package plub.plubserver.domain.recruit.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.recruit.model.Recruit;
import plub.plubserver.domain.recruit.model.RecruitSearchType;

import static plub.plubserver.domain.category.model.QPlubbingSubCategory.plubbingSubCategory;
import static plub.plubserver.domain.category.model.QSubCategory.subCategory;
import static plub.plubserver.domain.plubbing.model.QPlubbing.plubbing;
import static plub.plubserver.domain.recruit.model.QRecruit.recruit;

@RequiredArgsConstructor
public class RecruitRepositoryImpl implements RecruitRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Recruit> search(
            Pageable pageable,
            SortType sortType,
            RecruitSearchType type,
            String keyword
    ) {
        JPQLQuery<Recruit> prefixQuery = queryFactory.selectFrom(recruit)
                .leftJoin(recruit.plubbing, plubbing)
                .fetchJoin()
                .leftJoin(plubbing.plubbingSubCategories, plubbingSubCategory)
                .leftJoin(plubbingSubCategory.subCategory, subCategory)
                .distinct();

        JPQLQuery<Recruit> middleQuery = switch (type) {
            case TITLE -> prefixQuery.where(recruit.title.contains(keyword));
            case NAME -> prefixQuery.where(plubbing.name.contains(keyword));
            // MIX = TITLE_INTRO
            default -> prefixQuery.where(recruit.title.contains(keyword)
                    .or(recruit.introduce.contains(keyword)));
        };

        OrderSpecifier<?> order; // types : String, Integer
        if (sortType == SortType.POPULAR) {
            order = recruit.views.desc();
        } else {
            order = recruit.modifiedAt.desc();
        }
        return PageableExecutionUtils.getPage(middleQuery
                        .orderBy(order)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                middleQuery::fetchCount);
    }
}
