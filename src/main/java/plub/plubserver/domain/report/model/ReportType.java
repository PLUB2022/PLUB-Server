package plub.plubserver.domain.report.model;

public enum ReportType {
    // 🤬 비속어 / 폭언 / 비하 / 음란성 내용
    BAD_WORDS,
    // 🤥 갈등 조장 및 허위사실 유포
    FALSE_FACT,
    // 🤯 도배 / 광고성 내용 / 종교 권유
    ADVERTISEMENT,
    // ☹️ 그 외 기타사유
    ETC;

    public static ReportType toEnum(String stringParam) {
        return switch (stringParam.toUpperCase()) {
            case "BAD_WORDS" -> BAD_WORDS;
            case "FALSE_FACT" -> FALSE_FACT;
            case "ADVERTISEMENT" -> ADVERTISEMENT;
            case "ETC" -> ETC;
            default -> throw new IllegalArgumentException("ReportType is not valid.");
        };
    }

    public String getDetailContent() {
        return switch (this) {
            case BAD_WORDS -> "\uD83E\uDD2C 비속어 / 폭언 / 비하 / 음란성 내용";
            case FALSE_FACT -> "\uD83E\uDD25 갈등 조장 및 허위사실 유포";
            case ADVERTISEMENT -> "\uD83E\uDD2F 도배 / 광고성 내용 / 종교 권유";
            case ETC -> "☹\uFE0F 그 외 기타사유";
        };
    }
}
