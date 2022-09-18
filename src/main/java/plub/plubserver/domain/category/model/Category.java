package plub.plubserver.domain.category.model;

import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.plubing.model.Plubing;
import plub.plubserver.domain.recruit.model.Board;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String main;
    private String sub;

    // 카테고리(다) - 회원(1)
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    // 카테고리(1) - 모집(1) # 카테고리가 자식 -> 외래키는 모집이 관리
    @OneToOne(mappedBy = "category", cascade = CascadeType.ALL)
    private Board board;

    // 카테고리(1) - 모임(다)
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plubing> plubings = new ArrayList<>();
}
