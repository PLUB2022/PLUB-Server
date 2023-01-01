package plub.plubserver.domain.plubbing.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import plub.plubserver.domain.category.model.QPlubbingSubCategory;
import plub.plubserver.domain.category.model.QSubCategory;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.plubbing.model.Plubbing;
import plub.plubserver.domain.plubbing.model.PlubbingStatus;
import plub.plubserver.domain.plubbing.model.QPlubbing;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PlubbingRepositoryImpl implements PlubbingRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Plubbing> findAllByCategoryId(Long categoryId, Pageable pageable) {
        return PageableExecutionUtils.getPage(
                queryFactory
                        .selectFrom(QPlubbing.plubbing)
                        .join(QPlubbing.plubbing.plubbingSubCategories, QPlubbingSubCategory.plubbingSubCategory)
                        .join(QPlubbingSubCategory.plubbingSubCategory.subCategory, QSubCategory.subCategory)
                        .where(QSubCategory.subCategory.category.id.eq(categoryId),
                                QPlubbing.plubbing.status.eq(PlubbingStatus.ACTIVE),
                                QPlubbing.plubbing.visibility.eq(true))
                        .orderBy(QPlubbing.plubbing.modifiedAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(QPlubbing.plubbing)
                        .join(QPlubbing.plubbing.plubbingSubCategories, QPlubbingSubCategory.plubbingSubCategory)
                        .join(QPlubbingSubCategory.plubbingSubCategory.subCategory, QSubCategory.subCategory)
                        .where(QSubCategory.subCategory.category.id.eq(categoryId))
                        .fetch().size()
        );

    }

    @Override
    public Page<Plubbing> findAllBySubCategory(List<SubCategory> subCategories, Pageable pageable) {
        List<Plubbing> plubbings = new ArrayList<>();
        for (SubCategory s : subCategories) {
            plubbings.addAll(queryFactory
                    .selectFrom(QPlubbing.plubbing)
                    .join(QPlubbing.plubbing.plubbingSubCategories, QPlubbingSubCategory.plubbingSubCategory)
                    .where(QPlubbingSubCategory.plubbingSubCategory.subCategory.id.eq(s.getId()),
                            QPlubbing.plubbing.status.eq(PlubbingStatus.ACTIVE),
                            QPlubbing.plubbing.visibility.eq(true))
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
                        .selectFrom(QPlubbing.plubbing)
                        .where(QPlubbing.plubbing.status.eq(PlubbingStatus.ACTIVE),
                                QPlubbing.plubbing.visibility.eq(true))
                        .orderBy(QPlubbing.plubbing.views.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch(),
                pageable,
                () -> queryFactory
                        .selectFrom(QPlubbing.plubbing)
                        .where(QPlubbing.plubbing.status.eq(PlubbingStatus.ACTIVE),
                                QPlubbing.plubbing.visibility.eq(true))
                        .fetch().size()
        );
    }
}

