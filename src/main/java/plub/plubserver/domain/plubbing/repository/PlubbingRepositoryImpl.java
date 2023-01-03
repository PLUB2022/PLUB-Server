package plub.plubserver.domain.plubbing.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.category.model.QSubCategory;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.model.PlubbingStatus;

import java.util.ArrayList;
import java.util.List;

import static plub.plubserver.domain.category.model.QPlubbingSubCategory.plubbingSubCategory;
import static plub.plubserver.domain.plubbing.model.QPlubbing.plubbing;

@RequiredArgsConstructor
public class PlubbingRepositoryImpl implements PlubbingRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Plubbing> findAllByCategoryId(Long categoryId, Pageable pageable) {
        return PageableExecutionUtils.getPage(
                queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, QSubCategory.subCategory)
                        .where(QSubCategory.subCategory.category.id.eq(categoryId),
                                plubbing.status.eq(PlubbingStatus.ACTIVE),
                                plubbing.visibility.eq(true))
                        .orderBy(plubbing.modifiedAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(plubbing)
                        .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                        .join(plubbingSubCategory.subCategory, QSubCategory.subCategory)
                        .where(QSubCategory.subCategory.category.id.eq(categoryId))
                        .fetch().size()
        );

    }

    @Override
    public Page<Plubbing> findAllBySubCategory(List<SubCategory> subCategories, Pageable pageable) {
        List<Plubbing> plubbings = new ArrayList<>();
        for (SubCategory s : subCategories) {
            plubbings.addAll(queryFactory
                    .selectFrom(plubbing)
                    .join(plubbing.plubbingSubCategories, plubbingSubCategory)
                    .where(plubbingSubCategory.subCategory.id.eq(s.getId()),
                            plubbing.status.eq(PlubbingStatus.ACTIVE),
                            plubbing.visibility.eq(true))
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
}

