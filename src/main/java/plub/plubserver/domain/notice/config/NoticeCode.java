package plub.plubserver.domain.notice.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoticeCode {
    NOT_FOUND_NOTICE(404, 8510, "not found notice error."),
    NOT_FOUND_COMMENT(404, 8520, "not found notice comment error."),
    NOT_AUTHOR_ERROR(403, 8530, "not notice author error."),
    DELETED_STATUS_NOTICE(404, 8540, "deleted status notice error."),
    DELETED_STATUS_COMMENT(404, 8550, "deleted status notice comment error.");

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
