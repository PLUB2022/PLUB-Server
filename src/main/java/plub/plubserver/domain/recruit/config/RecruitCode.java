package plub.plubserver.domain.recruit.config;

import lombok.Getter;

@Getter
public enum RecruitCode {
    /**
     * success
     */

    /**
     * fail
     */
    NOT_FOUND_RECRUIT(404, 6050, "not found recruit error."),
    HOST_RECRUIT_ERROR(400, 6020, "forbidden access to plubbing error."),
    NOT_HOST(403, 6030, "not host error."),
    DELETED_STATUS_PLUBBING(404, 6040, "deleted/ended status error.");

    private final int HttpCode;
    private final int statusCode;
    private final String message;

    RecruitCode(int HttpCode, int statusCode, String message) {
        this.HttpCode = HttpCode;
        this.statusCode = statusCode;
        this.message = message;
    }
}
