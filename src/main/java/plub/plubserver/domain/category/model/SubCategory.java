package plub.plubserver.domain.category.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.AccountCategory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_Sub_id")
    private Long id;

    private int sequence;
    private String name;
    private String defaultImage;

    // 카테고리(1) - 서브 카테고리(다)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // 서브카테고리(1) - 모임 카테고리(다)
    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingSubCategory> plubbingCategories = new ArrayList<>();

    // 서브카테고리(1) - 회원 카테고리(다)
    @OneToMany(mappedBy = "categorySub", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountCategory> accountCategories = new ArrayList<>();

    public static SubCategory toSubCategory(String name, int sequence, Category category, String defaultImage) {
        return SubCategory.builder()
                .name(name)
                .sequence(sequence)
                .category(category)
                .defaultImage(defaultImage)
                .build();
    }
}
