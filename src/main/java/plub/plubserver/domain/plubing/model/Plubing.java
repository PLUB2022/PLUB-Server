package plub.plubserver.domain.plubing.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.activity.model.AccountPlubing;
import plub.plubserver.domain.recruit.model.Board;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plubing extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plubing_id")
    private Long id;

    // 모임(1) - 모집(1) # 모임이 부모 : 외래키 관리
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 모임(1) - 플러빙 공통(1) # 모임이 자식 -> 외래키는 공통이 관리
    @OneToOne(mappedBy = "plubing", cascade = CascadeType.ALL)
    private PlubingCommon plubingCommon;

    // 모임(1) - 플러빙 일정(다)
    @OneToMany(mappedBy = "plubing", cascade = CascadeType.ALL)
    private List<PlubingDate> plubingDateList = new ArrayList<>();

    // 모임(1) - 회원_모임페이지(다) # 다대다 용
    @OneToMany(mappedBy = "plubing", cascade = CascadeType.ALL)
    private List<AccountPlubing> accountPlubingList = new ArrayList<>();

    // 모임(1) - 플러빙 공지(다)
    @OneToMany(mappedBy = "plubing", cascade = CascadeType.ALL)
    private List<PlubingNotice> notices = new ArrayList<>();

}