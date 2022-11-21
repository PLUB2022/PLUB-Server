package plub.plubserver.domain.category.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlubbingSubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plubbing_category_id")
    private Long id;

    // 서브 카테고리(1) - 모임 카테고리(다)
    @ManyToOne
    @JoinColumn(name = "category_sub_id")
    private SubCategory subCategory;

    // 모임 페이지(1) - 모임 카테고리(다)
    @ManyToOne
    @JoinColumn(name = "plubbing_id")
    private Plubbing plubbing;

    @Builder
    public PlubbingSubCategory(SubCategory subCategory, Plubbing plubbing) {
        this.subCategory = subCategory;
        this.plubbing = plubbing;
    }
}
