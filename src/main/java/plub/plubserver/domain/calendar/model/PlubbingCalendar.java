package plub.plubserver.domain.calendar.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import plub.plubserver.common.model.BaseTimeEntity;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlubbingCalendar extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plubbing_calendar_id")
    private Long id;

    private String title;
    private String memo;

    private String staredAt;
    private String endedAt;

    private String startTime;
    private String endTime;
    private boolean isAllDay;

    private String address;
    private String roadAddress;
    private String placeName;

    private Long hostId;

    // 플러빙 일자(다) - 모임(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubbing_id")
    private Plubbing plubbing;

    // 플러빙 일자(1) - 참여자(다)
    @OneToMany(mappedBy = "plubbingCalendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlubbingCalendarAttend> accounts;
}
