package plub.plubserver.domain.category.model;

import plub.plubserver.domain.plubing.model.Plubing;

import javax.persistence.*;

@Entity
public class PlubingCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plubing_category_id")
    private Long id;

    // 서브 카테고리(1) - 모임 카테고리(다)
    @ManyToOne
    @JoinColumn(name = "category_sub_id")
    private CategorySub categorySub;

    // 모임 페이지(1) - 모임 카테고리(다)
    @ManyToOne
    @JoinColumn(name = "plubing_id")
    private Plubing plubing;

}
