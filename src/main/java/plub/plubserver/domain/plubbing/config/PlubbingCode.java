package plub.plubserver.domain.plubbing.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlubbingCode {
    NOT_FOUND_PLUBBING(404, 6010, "not found plubbing error."),
    FORBIDDEN_ACCESS_PLUBBING(403, 6020, "this account is not joined this plubbing."),
    NOT_HOST(403, 6030, "not host error."),
    DELETED_STATUS_PLUBBING(404, 6040, "deleted/ended status error."),
    NOT_MEMBER_ERROR(403, 6100, "this account is not a member of this plubbing.");


    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
