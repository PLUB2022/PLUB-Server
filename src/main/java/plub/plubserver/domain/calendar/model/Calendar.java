package plub.plubserver.domain.calendar.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.calendar.dto.CalendarDto;
import plub.plubserver.domain.plubbing.model.Plubbing;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calendar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id")
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
    @OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalendarAttend> calendarAttendList;

    public void addCalendarAttend(CalendarAttend calendarAttend) {
        if (calendarAttendList == null) {
            calendarAttendList = new ArrayList<>();
        }
        this.calendarAttendList.add(calendarAttend);
        calendarAttend.setCalendar(this);
    }

    public void updateCalendar(CalendarDto.UpdateCalendarRequest request) {
        this.title = request.title();
        this.memo = request.memo();
        this.staredAt = request.staredAt();
        this.endedAt = request.endedAt();
        this.startTime = request.startTime();
        this.endTime = request.endTime();
        this.isAllDay = request.isAllDay();
        this.address = request.address();
        this.roadAddress = request.roadAddress();
        this.placeName = request.placeName();
    }
}
