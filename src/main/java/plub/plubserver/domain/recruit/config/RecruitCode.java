package plub.plubserver.domain.recruit.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
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
    DELETED_STATUS_PLUBBING(404, 6040, "deleted/ended status error."),
    NOT_FOUND_QUESTION(404, 6050, "not found question error.");

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
