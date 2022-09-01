package plub.plubserver.domain.recruit.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.plubing.model.Plubing;
import plub.plubserver.domain.plubing.model.PlubingCommon;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title;
    private String introduce;
    private String days;
    private String onOff;
    private String place;
    private int maxNum;
    private int questionNum;

    // 모집(1) - 회원_모집페이지(다) # 다대다 용
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<AccountBoard> accountBoardList = new ArrayList<>();

    // 모집(1) - 모임(1) # 모집이 자식 -> 외래키는 모임이 관리
    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL)
    private Plubing plubing;

    // 모집(1) - 플러빙 공통(1) # 모집이 자식 -> 외래키는 공통이 관리
    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL)
    private PlubingCommon plubingCommon;

    // 모집(1) - 질문(다)
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();
}
