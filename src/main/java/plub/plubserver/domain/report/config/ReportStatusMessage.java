package plub.plubserver.domain.report.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static plub.plubserver.domain.report.config.ReportMessage.*;

@RequiredArgsConstructor
@Getter
public enum ReportStatusMessage {

    NORMAL(
            REPORT_FCM_TITLE_NORMAL,
            REPORT_FCM_CONTENT_NORMAL_PREFIX,
            REPORT_FCM_CONTENT_NORMAL_SUFFIX,
            REPORT_TITLE_PREFIX,
            REPORT_TITLE_NORMAL_SUFFIX,
            REPORT_CONTENT_PREFIX,
            REPORT_CONTENT_NORMAL_SUFFIX
    ),
    WARNING(
            REPORT_FCM_TITLE,
            REPORT_FCM_CONTENT_WARNING_PREFIX,
            REPORT_FCM_CONTENT_WARNING_SUFFIX,
            REPORT_TITLE_PREFIX,
            REPORT_TITLE_WARNING_SUFFIX,
            REPORT_CONTENT_PREFIX,
            REPORT_CONTENT_WARNING_SUFFIX
    ),
    PAUSED(
            REPORT_FCM_TITLE,
            REPORT_FCM_CONTENT_PAUSED_PREFIX,
            REPORT_FCM_CONTENT_PAUSED_SUFFIX,
            REPORT_TITLE_PREFIX,
            REPORT_TITLE_PAUSED_SUFFIX,
            REPORT_CONTENT_PREFIX,
            REPORT_CONTENT_PAUSED_SUFFIX
    ),
    BANNED(
            REPORT_FCM_TITLE,
            REPORT_FCM_CONTENT_BANNED_PREFIX,
            REPORT_FCM_CONTENT_BANNED_SUFFIX,
            REPORT_TITLE_PREFIX,
            REPORT_TITLE_BANNED_SUFFIX,
            REPORT_CONTENT_PREFIX,
            REPORT_CONTENT_BANNED_SUFFIX
    ),
    PERMANENTLY_BANNED(
            REPORT_FCM_TITLE,
            REPORT_FCM_CONTENT_PERMANENTLY_BANNED_PREFIX,
            REPORT_FCM_CONTENT_PERMANENTLY_BANNED_SUFFIX,
            REPORT_TITLE_PREFIX,
            REPORT_TITLE_PERMANENTLY_BANNED_SUFFIX,
            REPORT_CONTENT_PREFIX,
            REPORT_CONTENT_PERMANENTLY_BANNED_SUFFIX
    )
    ;


    private final String ReportFCMTitle;
    private final String ReportFCMContentPrefix;
    private final String ReportFCMContentSuffix;
    private final String ReportTitlePrefix;
    private final String ReportTitleSuffix;
    private final String ReportContentPrefix;
    private final String ReportContentSuffix;

    public String toFCMContent(String nickname) {
        return switch (this) {
            case NORMAL, WARNING, PAUSED, BANNED, PERMANENTLY_BANNED ->
                    this.ReportFCMContentPrefix + nickname + this.ReportFCMContentSuffix;
        };
    }
    public String toTitle(String nickname) {
        return switch (this) {
            case NORMAL, WARNING, PAUSED, BANNED, PERMANENTLY_BANNED -> this.ReportTitlePrefix + nickname + this.ReportTitleSuffix;
        };
    }

    public String toContent(String nickname, String content, String plubbing) {
        return switch (this) {
            case WARNING -> this.ReportContentPrefix + plubbing + "” 에서 ”" + content + this.ReportContentSuffix;
            case NORMAL, PAUSED, BANNED, PERMANENTLY_BANNED -> this.ReportContentPrefix + nickname + this.ReportContentSuffix;
        };
    }
}
