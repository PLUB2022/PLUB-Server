package plub.plubserver.domain.category.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import plub.plubserver.domain.category.model.Category;
import plub.plubserver.domain.category.model.SubCategory;

import java.util.List;

public class CategoryDto {
    public record CategoryResponse(
            @ApiModelProperty(value = "카테고리 id", example = "1")
            Long id,
            @ApiModelProperty(value = "이름", example = "예술")
            String name,
            @ApiModelProperty(value = "아이콘 이미지", example = "https://plub.s3.ap-northeast-2.amazonaws.com/category/categoryTest.png")
            String icon
    ) {
        @Builder
        public CategoryResponse {
        }

        public static CategoryResponse of(Category category) {
            return CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .icon(category.getIcon())
                    .build();
        }
    }

    public record SubCategoryResponse(
            @ApiModelProperty(value = "서브 카테고리 id", example = "1")
            Long id,
            @ApiModelProperty(value = "세부 카테고리 이름", example = "미술")
            String name,
            @ApiModelProperty(value = "카테고리 이름", example = "예술")
            String categoryName,
            @ApiModelProperty(value = "대분류 카테고리 id", example = "1")
            String parentId
    ) {
        @Builder
        public SubCategoryResponse {
        }

        public static SubCategoryResponse of(SubCategory categorySub) {
            return SubCategoryResponse.builder()
                    .id(categorySub.getId())
                    .name(categorySub.getName())
                    .categoryName(categorySub.getCategory().getName())
                    .parentId(categorySub.getCategory().getId().toString())
                    .build();
        }
    }

    public record AllCategoryResponse(
            Long id,
            String name,
            String icon,
            List<SubCategoryResponse> subCategories
    ) {
        @Builder
        public AllCategoryResponse {
        }

        public static AllCategoryResponse of(Category category, List<SubCategoryResponse> subCategories) {
            return AllCategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .icon(category.getIcon())
                    .subCategories(subCategories)
                    .build();
        }
    }

    public record CategoryListResponse(
            List<CategoryResponse> categories
    ) {
        @Builder
        public CategoryListResponse {
        }

        public static CategoryListResponse of(List<CategoryResponse> categories) {
            return CategoryListResponse.builder()
                    .categories(categories).build();
        }
    }

    public record SubCategoryListResponse(
            List<SubCategoryResponse> categories
    ) {
        @Builder
        public SubCategoryListResponse {
        }

        public static SubCategoryListResponse of(List<SubCategoryResponse> categories) {
            return SubCategoryListResponse.builder()
                    .categories(categories).build();
        }
    }

    public record AllCategoryListResponse(
            List<AllCategoryResponse> categories
    ) {
        @Builder
        public AllCategoryListResponse {
        }

        public static AllCategoryListResponse of(List<AllCategoryResponse> categories) {
            return AllCategoryListResponse.builder()
                    .categories(categories).build();
        }
    }

}