package plub.plubserver.domain.report.model;

public enum ReportTarget {
    ACCOUNT, // 계정
    PLUBBING, // 플럽밍
    RECRUIT, // 모집
    TODO, // 할일
    ARCHIVE, // 아카이브
    FEED, // 피드
    COMMENT; // 댓글

    public static ReportTarget toEnum(String stringParam) {
        return switch (stringParam.toUpperCase()) {
            case "ACCOUNT" -> ACCOUNT;
            case "PLUBBING" -> PLUBBING;
            case "RECRUIT" -> RECRUIT;
            case "TODO" -> TODO;
            case "ARCHIVE" -> ARCHIVE;
            case "FEED" -> FEED;
            case "COMMENT" -> COMMENT;
            default -> throw new IllegalArgumentException("ReportTarget is not valid.");
        };
    }
}
