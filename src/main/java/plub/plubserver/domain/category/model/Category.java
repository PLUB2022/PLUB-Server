package plub.plubserver.domain.category.model;

import lombok.*;
import plub.plubserver.common.model.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;
    private int sequence;
    private String icon;

    // 카테고리(1) - 서브 카테고리(다)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategorySub> categorySubs = new ArrayList<>();
}
