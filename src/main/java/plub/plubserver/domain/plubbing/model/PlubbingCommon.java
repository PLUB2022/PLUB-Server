package plub.plubserver.domain.plubbing.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.recruit.model.Board;

import javax.persistence.*;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlubbingCommon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plubbing_common_id")
    private Long id;

    private String name;
    private String goal;
    private String mainImg;
    private int maxNum;

    // 플러빙 공통(1) - 모집(1) # 공통이 부모 : 외래키 관리
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 플러빙 공통(1) - 모임(1) # 공통이 부모 : 외래키 관리
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubbing_id")
    private Plubbing plubbing;
}
