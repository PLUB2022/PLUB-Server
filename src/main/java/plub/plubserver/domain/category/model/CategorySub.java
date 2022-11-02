package plub.plubserver.domain.category.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.domain.recruit.model.Board;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategorySub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_Sub_id")
    private Long id;

    private int sequence;
    private String name;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp modifiedAt;

    // 카테고리(1) - 서브 카테고리(다)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // 서브카테고리(1) - 모집(1) # 카테고리가 자식 -> 외래키는 모집이 관리
    @OneToOne(mappedBy = "categorySub", cascade = CascadeType.ALL)
    private Board board;

    // 서브카테고리(1) - 모임 카테고리(다)
    @OneToMany(mappedBy = "categorySub", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubingCategory> plubingCategories = new ArrayList<>();

    // 서브카테고리(1) - 회원 카테고리(다)
    @OneToMany(mappedBy = "categorySub", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountCategory> accountCategories = new ArrayList<>();
}
