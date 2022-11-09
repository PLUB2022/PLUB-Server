package plub.plubserver.domain.category.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import plub.plubserver.domain.category.model.Category;
import plub.plubserver.domain.category.model.CategorySub;

public class CategoryDto {
    public record CategoryListResponse(
            @ApiModelProperty(value = "카테고리 id", example = "1")
            Long id,
            @ApiModelProperty(value = "이름", example = "예술")
            String name,
            @ApiModelProperty(value = "순서", example = "1")
            int sequence,
            @ApiModelProperty(value = "아이콘 이미지", example = "https://plub.s3.ap-northeast-2.amazonaws.com/category/categoryTest.png")
            String icon,
            @ApiModelProperty(value = "수정 날짜", example = "2022-10-31 20:19:56")
            String modifiedAt
    ){
        @Builder
        public CategoryListResponse {}
        public static CategoryListResponse of(Category category) {
            return CategoryListResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .sequence(category.getSequence())
                    .icon(category.getIcon())
                    .modifiedAt(category.getModifiedAt())
                    .build();
        }
    }
    public record CategorySubListResponse(
            @ApiModelProperty(value = "순서", example = "1")
            int sequence,
            @ApiModelProperty(value = "세부 카테고리 이름", example = "미술")
            String name,
            @ApiModelProperty(value = "카테고리 이름", example = "예술")
            String categoryName,
            @ApiModelProperty(value = "수정 날짜", example = "2022-10-31 20:19:56")
            String modifiedAt
    ){
        @Builder
        public CategorySubListResponse {}
        public static CategorySubListResponse of(CategorySub categorySub) {
            return CategorySubListResponse.builder()
                    .sequence(categorySub.getSequence())
                    .name(categorySub.getName())
                    .categoryName(categorySub.getCategory().getName())
                    .modifiedAt(categorySub.getModifiedAt())
                    .build();
        }
    }
    public record CategoryVersionResponse(
            @ApiModelProperty(value = "가장 최근 수정 날짜", example = "2022-10-31 20:19:56")
            String lastModifiedAt,
            @ApiModelProperty(value = "카테고리 종류(메인/서브)", example = "category")
            String categoryType
    ){
        @Builder
        public CategoryVersionResponse {}
        public static CategoryVersionResponse of(String modifiedAt, String categoryType) {
            return CategoryVersionResponse.builder()
                    .lastModifiedAt(modifiedAt)
                    .categoryType(categoryType)
                    .build();
        }
    }
}