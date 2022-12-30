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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public SubCategory getSubCategory(String categoryName) {
        return subCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new CategoryException(CategoryCode.NOT_FOUND_CATEGORY));
    }

    public List<CategoryListResponse> getCategoryList() {
        return categoryRepository.findAll()
                .stream().map(CategoryListResponse::of)
                .collect(Collectors.toList());
    }

    public List<SubCategoryListResponse> getSubCategoryList(Long categoryId) {
        return subCategoryRepository.findAllByCategoryId(categoryId)
                .stream().map(SubCategoryListResponse::of)
                .collect(Collectors.toList());
    }

    public boolean createCategory(String name, int sequence, String icon) {
        Category category = Category.toCategory(name, sequence, icon);
        categoryRepository.save(category);
        return true;
    }

    public boolean createSubCategory(String name, int sequence, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryCode.NOT_FOUND_CATEGORY));
        SubCategory categorySub = SubCategory.toCategorySub(name, sequence, category);
        subCategoryRepository.save(categorySub);
        return true;
    }

    public List<AllCategoryResponse> getAllCategory() {
        List<Category> categories = categoryRepository.findAll().stream().collect(Collectors.toList());
        List<AllCategoryResponse> allCategoryResponses = new ArrayList<>();
        for(Category c : categories){
            allCategoryResponses.add(AllCategoryResponse.of(c, subCategoryRepository.findAllByCategoryId(c.getId()).
                            stream().map(SubCategoryListResponse::of).collect(Collectors.toList())));
        }
        return allCategoryResponses;
    }
}