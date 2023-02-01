package plub.plubserver.domain.plubbing.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.util.List;

public interface PlubbingRepositoryCustom {
    Page<Plubbing> findAllByCategoryId(Long categoryId, Pageable pageable, SortType sortType);

    Page<Plubbing> findAllBySubCategory(List<SubCategory> subCategories, Pageable pageable);

    Page<Plubbing> findAllByViews(Pageable pageable);
}

