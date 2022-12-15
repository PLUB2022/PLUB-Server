package plub.plubserver.domain.category.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import plub.plubserver.domain.category.model.Category;
import plub.plubserver.domain.category.model.SubCategory;

public class CategoryDto {
    public record CategoryListResponse(
            @ApiModelProperty(value = "카테고리 id", example = "1")
            Long id,
            @ApiModelProperty(value = "이름", example = "예술")
            String name,
            @ApiModelProperty(value = "순서", example = "1")
            int sequence,
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
                    .sequence(category.getSequence())
                    .icon(category.getIcon())
                    .build();
        }
    }

    public record SubCategoryListResponse(
            @ApiModelProperty(value = "서브 카테고리 id", example = "1")
            Long id,
            @ApiModelProperty(value = "순서", example = "1")
            int sequence,
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
                    .sequence(categorySub.getSequence())
                    .name(categorySub.getName())
                    .categoryName(categorySub.getCategory().getName())
                    .build();
        }
    }
}