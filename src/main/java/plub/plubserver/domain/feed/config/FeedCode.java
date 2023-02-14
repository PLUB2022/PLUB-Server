package plub.plubserver.domain.feed.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedCode {
    NOT_FOUND_FEED(404, 8010, "not found feed error."),
    NOT_FOUND_COMMENT(404, 8020, "not found feed comment error."),
    NOT_AUTHOR_ERROR(403, 8030, "not feed author error."),
    DELETED_STATUS_FEED(404, 8040, "deleted status feed error."),
    CANNOT_DELETED_FEED(404, 8050, "system feed cannot be deleted."),
    DELETED_STATUS_COMMENT(404, 8060, "deleted status feed comment error."),
    MAX_FEED_PIN(404, 8070, "max feed pin error.");

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
