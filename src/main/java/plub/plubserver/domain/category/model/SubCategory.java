package plub.plubserver.domain.category.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.account.model.AccountCategory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_Sub_id")
    private Long id;

    private int sequence;
    private String name;

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

    @Builder
    public SubCategory(int sequence, String name, Category category) {
        this.sequence = sequence;
        this.name = name;
        this.category = category;
    }

    public static SubCategory toCategorySub(String name, int sequence, Category category) {
        return SubCategory.builder()
                .name(name)
                .sequence(sequence)
                .category(category)
                .build();
    }
}
