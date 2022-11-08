package plub.plubserver.domain.category.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.category.dto.CategoryDto.*;
import plub.plubserver.domain.category.service.CategoryService;

import javax.validation.Valid;
import java.util.List;

import static plub.plubserver.common.dto.ApiResponse.success;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Api(tags = "카테고리 API", hidden = true)
public class CategoryController {
    private final CategoryService categoryService;

    @ApiOperation(value = "카테고리 버전 체크")
    @GetMapping("/check/version")
    public ApiResponse<CategoryVersionResponse> getCategoryVersion() {
        return success(categoryService.getCategoryVersion(), "카테고리 버전 조회");
    }

    @ApiOperation(value = "카테고리 전체 조회")
    @GetMapping
    public ApiResponse<List<CategoryListResponse>> getAllCategory() {
        return success(categoryService.getAllCategory(), "카테고리 전체 조회");
    }

   @ApiOperation(value = "서브 카테고리 조회")
    @GetMapping("/sub")
    public ApiResponse<List<CategorySubListResponse>> getAllCategorySub(@RequestParam Long category_id) {
        return success(categoryService.getAllCategorySub(category_id), "서브 카테고리 조회");
    }
}
