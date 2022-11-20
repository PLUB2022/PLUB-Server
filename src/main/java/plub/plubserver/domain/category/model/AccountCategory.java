package plub.plubserver.domain.category.model;

import plub.plubserver.domain.account.model.Account;
import javax.persistence.*;

@Entity
public class AccountCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_category_id")
    private Long id;

    // 서브 카테고리(1) - 회원 카테고리(다)
    @ManyToOne
    @JoinColumn(name = "category_sub_id")
    private SubCategory categorySub;

    // 회원(1) - 회원 카테고리(다)
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
