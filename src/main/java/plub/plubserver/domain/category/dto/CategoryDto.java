package plub.plubserver.domain.category.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import plub.plubserver.domain.category.model.Category;
import plub.plubserver.domain.category.model.SubCategory;

import java.util.List;

public class CategoryDto {
    public record CategoryListResponse(
            @ApiModelProperty(value = "카테고리 id", example = "1")
            Long id,
            @ApiModelProperty(value = "이름", example = "예술")
            String name,
            @ApiModelProperty(value = "아이콘 이미지", example = "https://plub.s3.ap-northeast-2.amazonaws.com/category/categoryTest.png")
            String icon
    ) {
        @Builder
        public CategoryListResponse {
        }

        public static CategoryListResponse of(Category category) {
            return CategoryListResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .icon(category.getIcon())
                    .build();
        }
    }

    public record SubCategoryListResponse(
            @ApiModelProperty(value = "서브 카테고리 id", example = "1")
            Long id,
            @ApiModelProperty(value = "세부 카테고리 이름", example = "미술")
            String name,
            @ApiModelProperty(value = "카테고리 이름", example = "예술")
            String categoryName
    ) {
        @Builder
        public SubCategoryListResponse {
        }

        public static SubCategoryListResponse of(SubCategory categorySub) {
            return SubCategoryListResponse.builder()
                    .id(categorySub.getId())
                    .name(categorySub.getName())
                    .categoryName(categorySub.getCategory().getName())
                    .build();
        }
    }

    public record AllCategoryResponse(
            Long id,
            String name,
            String icon,
            List<SubCategoryListResponse> subCategories
    ) {
        @Builder
        public AllCategoryResponse {
        }

        public static AllCategoryResponse of(Category category, List<SubCategoryListResponse> subCategories) {
            return AllCategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .icon(category.getIcon())
                    .subCategories(subCategories)
                    .build();
        }
    }
}