package plub.plubserver.domain.plubbing.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlubbingCode {
    /**
     * success
     */

    /**
     * fail
     */
    NOT_FOUND_PLUBBING(404, 6010, "not found plubbing error."),
    FORBIDDEN_ACCESS_PLUBBING(403, 6020, "forbidden access to plubbing error."),
    NOT_HOST(403, 6030, "not host error."),
    DELETED_STATUS_PLUBBING(404, 6040, "deleted/ended status error.");

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
