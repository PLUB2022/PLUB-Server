package plub.plubserver.domain.report.config;

public class ReportMessage {
    public static final String REPORT_FCM_TITLE = "신고";
    public static final String REPORT_FCM_TITLE_NORMAL = "정지 해제";

    public static final String REPORT_FCM_CONTENT_NORMAL_PREFIX = "“";
    public static final String REPORT_FCM_CONTENT_NORMAL_SUFFIX = "” 님, 이용 정지가 해제되었습니다. PLUB과 함께 즐거운 시간 보내 보아요\uD83D\uDE09";
    public static final String REPORT_FCM_CONTENT_WARNING_PREFIX = "“";
    public static final String REPORT_FCM_CONTENT_WARNING_SUFFIX = "님에 대한 신고가 접수되었습니다. 탭을 눌러 자세한 사항을 확인해주세요. ";
    public static final String REPORT_FCM_CONTENT_PAUSED_PREFIX = "3회 이상 다른 사용자의 신고가 누적되어 “";
    public static final String REPORT_FCM_CONTENT_PAUSED_SUFFIX = "” 님의 앱 이용이 일시적으로 정지되었습니다. ";
    public static final String REPORT_FCM_CONTENT_BANNED_PREFIX = "“";
    public static final String REPORT_FCM_CONTENT_BANNED_SUFFIX = "” 님의 앱 이용이 한 달 간 정지되었습니다. 한 달 후 정지가 해제됩니다. ";
    public static final String REPORT_FCM_CONTENT_PERMANENTLY_BANNED_PREFIX = "6회 이상 다른 사용자의 신고가 누적되어 “";
    public static final String REPORT_FCM_CONTENT_PERMANENTLY_BANNED_SUFFIX = "” 님의 앱 이용이 영구정지 되었습니다. 앞으로 앱 이용이 불가한 점 고지드립니다.";
    public static final String REPORT_TITLE_ADMIN = "검토 완료";
    public static final String REPORT_TITLE_PREFIX = "[신고] “";

    public static final String REPORT_TITLE_NORMAL_SUFFIX = "”님의 이용 정지가 해제되었습니다. ";
    public static final String REPORT_TITLE_WARNING_SUFFIX = "”님에 대한 신고가 접수되었습니다. ";
    public static final String REPORT_TITLE_PAUSED_SUFFIX = "”님의 계정이 일시적으로 정지되었습니다. ";
    public static final String REPORT_TITLE_BANNED_SUFFIX = "”님의 계정이 한 달 간 정지되었습니다. ";
    public static final String REPORT_TITLE_PERMANENTLY_BANNED_SUFFIX = "”님의 계정이 영구정지 되었습니다. ";

    public static final String REPORT_CONTENT_PREFIX = "“";

    public static final String REPORT_CONTENT_NORMAL_SUFFIX = "” 님, 이용 정지가 해제되었습니다. PLUB과 함께 즐거운 시간 보내 보아요\uD83D\uDE09";
    public static final String REPORT_CONTENT_WARNING_SUFFIX = "” 를 사유로 신고가 접수되었습니다. 아래 신고 정책을 참고하여 주의 부탁드립니다. 만약 접수된 신고가 허위일 경우, 고객센터에서 문의를 통하여 신고 횟수를 차감하실 수 있습니다. \n" +
            "\n" +
            "신고 정책\n" +
            "1회: 경고\n" +
            "3회: 한 달 정지\n" +
            "6회: 영구 정지\n" +
            "\n" +
            " 계정이 영구정지 될 경우 재가입 및 재이용이 불가하므로, 접수된 신고가 허위일 경우 사전에 고객센터로 문의주시고 신고 횟수를 차감하시길 권고드립니다.";

    public static final String REPORT_CONTENT_PAUSED_SUFFIX = "“ 님에 대한 신고가 3회 이상 누적되어 계정이 일시적으로 정지되었습니다. 아래 사항을 확인하시고 접수된 신고가 허위일 경우, 고객센터에서 문의를 통하여 정지를 즉시 해제하실 수 있습니다.\n" +
            "\n" +
            "신고 사유\n" +
            "1차: “신고 사유\", “모임 이름\"\n" +
            "2차: “신고 사유\", “모임 이름\"\n" +
            "3차: “신고 사유\", “모임 이름\"\n" +
            "\n" +
            " 6회 이상 신고되어 계정이 영구정지 될 경우 재가입 및 재이용이 불가합니다. 접수된 신고가 허위일 경우 사전에 고객센터로 문의주시고 신고 횟수를 차감하시길 권고드립니다.";


    public static final String REPORT_CONTENT_BANNED_SUFFIX = "“ 님에 대한 신고가 3회 이상 누적되어 계정이 한 달 간 정지되었습니다.  6회 이상 신고되어 계정이 영구정지 될 경우 재가입 및 재이용이 불가합니다. \n" +
            "\n" +
            " PLUB은 신뢰할 수 있는 커뮤니티를 지향합니다. 앞으로 이 점 주의하여 활동 부탁드리며, 한 달 후에 뵙겠습니다. ";
    public static final String REPORT_CONTENT_PERMANENTLY_BANNED_SUFFIX = "“ 님에 대한 다른 사용자의 신고가 6회 이상 누적되어 계정이 영구정지되었습니다.  PLUB 재가입 및 재이용이 불가합니다. 지금까지 PLUB을 이용해주셔서 감사합니다. ";
}
