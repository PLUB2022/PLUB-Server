package plub.plubserver.domain.report.model;

public enum ReportTarget {
    ACCOUNT, // 계정(프로필)
    RECRUIT, // 모집
    TODO, // 할일
    ARCHIVE, // 아카이브
    FEED, // 피드
    FEED_COMMENT, // 피드 댓글
    NOTICE_COMMENT; // 공지 댓글

    public static ReportTarget toEnum(String stringParam) {
        return switch (stringParam.toUpperCase()) {
            case "ACCOUNT" -> ACCOUNT;
            case "RECRUIT" -> RECRUIT;
            case "TODO" -> TODO;
            case "ARCHIVE" -> ARCHIVE;
            case "FEED" -> FEED;
            case "FEED_COMMENT" -> FEED_COMMENT;
            case "NOTICE_COMMENT" -> NOTICE_COMMENT;
            default -> throw new IllegalArgumentException("ReportTarget is not valid.");
        };
    }
}
