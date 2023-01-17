package plub.plubserver.domain.account.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountCode {
    // Account
    NOT_FOUND_ACCOUNT(404, 2050, "not found account error."),
    NICKNAME_DUPLICATION(400, 2060, "duplicated nickname error."),
    EMAIL_DUPLICATION(400, 2070, "duplicated email error."),
    SOCIAL_TYPE_ERROR(400, 2090, "invalid social type error."),
    ROLE_ACCESS_ERROR(400, 2120, "role access error."),
    NICKNAME_ERROR(400, 2130, "invalid nickname error.");

    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
