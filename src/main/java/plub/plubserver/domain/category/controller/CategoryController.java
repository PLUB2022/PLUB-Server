package plub.plubserver.domain.category.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.category.dto.CategoryDto.AllCategoryListResponse;
import plub.plubserver.domain.category.dto.CategoryDto.CategoryListResponse;
import plub.plubserver.domain.category.dto.CategoryDto.SubCategoryListResponse;
import plub.plubserver.domain.category.service.CategoryService;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Api(tags = "카테고리 API", hidden = true)
public class CategoryController {
    private final CategoryService categoryService;

    @ApiOperation(value = "카테고리 목록 조회")
    @GetMapping
    public ApiResponse<CategoryListResponse> getCategoryList() {
        return success(categoryService.getCategoryList());
    }

    @ApiOperation(value = "서브 카테고리 목록 조회")
    @GetMapping("/{categoryId}/sub")
    public ApiResponse<SubCategoryListResponse> getSubCategoryList(@PathVariable Long categoryId) {
        return success(categoryService.getSubCategoryList(categoryId));
    }

    @ApiOperation(value = "카테고리 전체 조회")
    @GetMapping("/all")
    public ApiResponse<AllCategoryListResponse> getAllCategory() {
        return success(categoryService.getAllCategory());
    }
}