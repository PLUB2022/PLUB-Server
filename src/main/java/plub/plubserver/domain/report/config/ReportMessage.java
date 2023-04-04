package plub.plubserver.domain.report.config;

public class ReportMessage {
    public static final String REPORT_SUCCESS = "신고가 접수되었습니다.";
    public static final String REPORT_HOST_NOTIFY = "신고가 접수되었습니다. 신고 누적 6회 이상이라 호스트에게 경고를 보냈습니다.";
    public static final String REPORT_PLUBBING_PAUSED = "신고가 접수되었습니다. 모임장 경고 3회 이상이라 모임이 영구 정지되었습니다.";

    public static final String REPORT_ACCOUNT_WARNING = "신고가 접수되었습니다. 계정 경고 1회 입니다. 3회 시 1달 정지 6회시 영구정지가 됩니다.";
    public static final String REPORT_ACCOUNT_PAUSED = "신고가 접수되었습니다. 계정 경고 3회 이상이라 계정이 정지되었습니다.";

    public static final String REPORT_ACCOUNT_BANNED = "신고가 접수되었습니다. 계정 경고 6회 이상이라 계정이 영구 정지되었습니다.";


    public static final String REPORT_ACCOUNT_WARING_CONTENT = "경고 1회 입니다. 경고 3회 누적시 계정이 1개월 정지 됩니다.";

    public static final String REPORT_ACCOUNT_PAUSED_CONTENT = "경고 3회 누적으로 계정이 1개월 정지 되었습니다.";
    public static final String REPORT_ACCOUNT_BAN_CONTENT = "경고 6회 누적으로 계정이 영구 정지 되었습니다.";

    public static final String ADMIN_REPORT_ACCOUNT_BAN = "신고가 접수되었습니다. 계정이 영구 정지 되었습니다.";


}
