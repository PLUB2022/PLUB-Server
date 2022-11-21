package plub.plubserver.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.category.dto.CategoryDto.*;
import plub.plubserver.domain.category.config.CategoryCode;
import plub.plubserver.domain.category.exception.CategoryException;
import plub.plubserver.domain.category.model.Category;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.category.repository.CategoryRepository;
import plub.plubserver.domain.category.repository.SubCategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

import static plub.plubserver.domain.category.config.CategoryCode.NOT_FOUND_CATEGORY;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public SubCategory getSubCategory(String categoryName) {
        return subCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CategoryException(NOT_FOUND_CATEGORY));
    }

    public List<CategoryListResponse> getAllCategory() {
        return categoryRepository.findAll()
                .stream().map(CategoryListResponse::of)
                .collect(Collectors.toList());
    }
    public List<SubCategoryListResponse> getAllCategorySub(Long categoryId) {
        return subCategoryRepository.findAllByCategoryId(categoryId)
                .stream().map(SubCategoryListResponse::of)
                .collect(Collectors.toList());
    }

    /*public CategoryVersionResponse getCategoryVersion() {
        String categoryLatestDate = categoryRepository.getLatestDate()
                .orElseThrow(()->new CategoryException(CategoryCode.NOT_FOUND_CATEGORY));
        String categorySubLatestDate = subCategoryRepository.getLatestDate()
                .orElseThrow(()->new CategoryException(CategoryCode.NOT_FOUND_CATEGORY));

        if(categoryLatestDate.compareTo(categorySubLatestDate)<0)
            return CategoryVersionResponse.of(categorySubLatestDate, "categorySub");
        else
            return CategoryVersionResponse.of(categoryLatestDate, "category");
    }*/

    public boolean createCategory(String name, int sequence, String icon) {
        Category category = Category.toCategory(name, sequence, icon);
        categoryRepository.save(category);
        return true;
    }

    public boolean createSubCategory(String name, int sequence, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new CategoryException(CategoryCode.NOT_FOUND_CATEGORY));
        SubCategory categorySub = SubCategory.toCategorySub(name, sequence, category);
        subCategoryRepository.save(categorySub);
        return true;
    }
}