package plub.plubserver.domain.plubbing.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MeetingDay {
    MON("월"),
    TUE("화"),
    WED("수"),
    THR("목"),
    FRI("금"),
    SAT("토"),
    SUN("일"),
    ALL("전체");

    private final String korean;

    public String toKorean() {
        return korean;
    }
}
