package plub.plubserver.domain.calendar.model;

import lombok.*;
import plub.plubserver.common.model.BaseEntity;
import plub.plubserver.domain.account.model.Account;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CalendarAttend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_attend")
    private Long id;

    @Enumerated(EnumType.STRING)
    private AttendStatus attendStatus;

    // 참여자(다) - 플러빙일정(1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private Calendar calendar;

    // 참여자(1) - 참석여부(다)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void updateAttendStatus(AttendStatus attendStatus) {
        this.attendStatus = attendStatus;
    }
}
