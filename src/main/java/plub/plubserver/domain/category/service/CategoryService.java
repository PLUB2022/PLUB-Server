package plub.plubserver.domain.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.category.dto.CategoryDto.*;
import plub.plubserver.domain.category.exception.CategoryError;
import plub.plubserver.domain.category.exception.CategoryException;
import plub.plubserver.domain.category.repository.CategoryRepository;
import plub.plubserver.domain.category.repository.CategorySubRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategorySubRepository categorySubRepository;

    public List<CategoryListResponse> getAllCategory() {
        return categoryRepository.findAll()
                .stream().map(CategoryListResponse::of)
                .collect(Collectors.toList());
    }

    public List<CategorySubListResponse> getAllCategorySub(Long category_id) {
        return categorySubRepository.findAllByCategoryId(category_id)
                .stream().map(CategorySubListResponse::of)
                .collect(Collectors.toList());
    }

    public CategoryVersionResponse getCategoryVersion() {
        String categoryLatestDate = categoryRepository.getLatestDate()
                .orElseThrow(()->new CategoryException(CategoryError.NOT_FOUND_CATEGORY));
        String categorySubLatestDate = categorySubRepository.getLatestDate()
                .orElseThrow(()->new CategoryException(CategoryError.NOT_FOUND_CATEGORY));

       if(categoryLatestDate.compareTo(categorySubLatestDate)<0)
           return CategoryVersionResponse.of(categorySubLatestDate, "categorySub");
       else
           return CategoryVersionResponse.of(categoryLatestDate, "category");
    }
}
