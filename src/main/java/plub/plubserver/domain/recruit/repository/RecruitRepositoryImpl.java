package plub.plubserver.domain.recruit.repository;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.plubbing.model.PlubbingStatus;
import plub.plubserver.domain.recruit.model.Recruit;

import static plub.plubserver.domain.category.model.QPlubbingSubCategory.plubbingSubCategory;
import static plub.plubserver.domain.category.model.QSubCategory.subCategory;
import static plub.plubserver.domain.plubbing.model.QPlubbing.plubbing;
import static plub.plubserver.domain.recruit.model.QRecruit.recruit;

@RequiredArgsConstructor
public class RecruitRepositoryImpl implements RecruitRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Recruit> search(Pageable pageable, String keyword) {
        JPQLQuery<Recruit> middleQuery = queryFactory.selectFrom(recruit)
                .leftJoin(recruit.plubbing, plubbing)
                .fetchJoin()
                .leftJoin(plubbing.plubbingSubCategories, plubbingSubCategory)
                .leftJoin(plubbingSubCategory.subCategory, subCategory)
                .where(plubbing.status.eq(PlubbingStatus.ACTIVE),
                        plubbing.visibility.eq(true),
                        (recruit.title.contains(keyword)
                                .or(subCategory.category.name.contains(keyword)
                                        .or(recruit.introduce.contains(keyword)
                                        )
                                ))
                );
        return PageableExecutionUtils.getPage(middleQuery
                        .orderBy(plubbing.modifiedAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                middleQuery::fetchCount);
    }
}
