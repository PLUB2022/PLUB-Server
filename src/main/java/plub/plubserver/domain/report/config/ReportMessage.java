package plub.plubserver.domain.report.config;

public class ReportMessage {
    public static final String REPORT_SUCCESS = "신고가 접수되었습니다.";
    public static final String REPORT_HOST_NOTIFY = "신고가 접수되었습니다. 신고 누적 6회 이상이라 호스트에게 경고를 보냈습니다.";
    public static final String REPORT_PLUBBING_PAUSED = "신고가 접수되었습니다. 모임장 경고 3회 이상이라 모임이 영구 정지되었습니다.";
}
