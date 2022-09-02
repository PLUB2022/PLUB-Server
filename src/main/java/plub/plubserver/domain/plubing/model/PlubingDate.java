package plub.plubserver.domain.plubing.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlubingDate extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plubing_date_id")
    private Long id;

    private String date;
    private String time;
    private String place;

    // 플러빙 일자(다) - 모임(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubing_id")
    private Plubing plubing;
}
