package plub.plubserver.domain.plubbing.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlubbingMeetingDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plubbing_meeting_day_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plubbing_id")
    private Plubbing plubbing;

    private MeetingDay day;

    public PlubbingMeetingDay(String day, Plubbing plubbing) {
        this.day = switch (day) {
            case "MON" -> MeetingDay.MON;
            case "TUE" -> MeetingDay.TUE;
            case "WED" -> MeetingDay.WED;
            case "THR" -> MeetingDay.THR;
            case "FRI" -> MeetingDay.FRI;
            case "SAT" -> MeetingDay.SAT;
            case "SUN" -> MeetingDay.SUN;
            default -> MeetingDay.ALL;
        };
        this.plubbing = plubbing;
    }

    public void mapPlubbing(Plubbing plubbing) {
        this.plubbing = plubbing;
    }
}
