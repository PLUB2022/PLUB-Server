package plub.plubserver.domain.recruit.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RecruitSearchType {
    TITLE, MIX, NAME;
    public static RecruitSearchType toType(String stringParam) {
        return switch (stringParam) {
            case "title" -> TITLE;
            case "name" -> NAME;
            // mix
            default -> MIX;
        };
    }
}
