package plub.plubserver.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.category.dto.CategoryDto.*;
import plub.plubserver.domain.category.config.CategoryCode;
import plub.plubserver.domain.category.exception.CategoryException;
import plub.plubserver.domain.category.model.Category;
import plub.plubserver.domain.category.model.CategorySub;
import plub.plubserver.domain.category.repository.CategoryRepository;
import plub.plubserver.domain.category.repository.CategorySubRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategorySubRepository categorySubRepository;

    public List<CategoryListResponse> getAllCategory() {
        return categoryRepository.findAll()
                .stream().map(CategoryListResponse::of)
                .collect(Collectors.toList());
    }
    public List<CategorySubListResponse> getAllCategorySub(Long categoryId) {
        return categorySubRepository.findAllByCategoryId(categoryId)
                .stream().map(CategorySubListResponse::of)
                .collect(Collectors.toList());
    }

    /*public CategoryVersionResponse getCategoryVersion() {
        String categoryLatestDate = categoryRepository.getLatestDate()
                .orElseThrow(()->new CategoryException(CategoryCode.NOT_FOUND_CATEGORY));
        String categorySubLatestDate = categorySubRepository.getLatestDate()
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

    public boolean createCategorySub(String name, int sequence, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new CategoryException(CategoryCode.NOT_FOUND_CATEGORY));
        CategorySub categorySub = CategorySub.toCategorySub(name, sequence, category);
        categorySubRepository.save(categorySub);
        return true;
    }
}