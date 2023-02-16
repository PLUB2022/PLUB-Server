package plub.plubserver.domain.plubbing.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import plub.plubserver.common.model.SortType;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.plubbing.model.Plubbing;

import java.util.List;

public interface PlubbingRepositoryCustom {
    Page<Plubbing> findAllBySubCategory(List<SubCategory> subCategories, Pageable pageable);

    Page<Plubbing> findAllByViews(Pageable pageable);

    Page<Plubbing> findAllByCategoryId(Long categoryId, Pageable pageable, SortType sortType);

    Page<Plubbing> findAllByCategoryIdAndAccountNum(Long categoryId, Integer accountNum, Pageable pageable, SortType sortType);

    Page<Plubbing> findAllByCategoryIdAndDays(Long categoryId, List<String> days, Pageable pageable, SortType sortType);

    Page<Plubbing> findAllByCategoryIdAndSubCategoryId(Long categoryId, List<Long> subCategoryId, Pageable pageable, SortType sortType);

    Page<Plubbing> findAllByCategoryIdAndDaysAndAccountNum(Long categoryId, List<String> days, Integer accountNum, Pageable pageable, SortType sortType);

    Page<Plubbing> findAllByCategoryIdAndSubCategoryIdAndAccountNum(Long categoryId, List<Long> subCategoryId, Integer accountNum, Pageable pageable, SortType sortType);

    Page<Plubbing> findAllByCategoryIdAndSubCategoryIdAndDays(Long categoryId, List<Long> subCategoryId, List<String> days, Pageable pageable, SortType sortType);

    Page<Plubbing> findAllByCategoryIdAndSubCategoryIdAndDaysAndAccountNum(Long categoryId, List<Long> subCategoryId, List<String> days, Integer accountNum, Pageable pageable, SortType sortType);
}

