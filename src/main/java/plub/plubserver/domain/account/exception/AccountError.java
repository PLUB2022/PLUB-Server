package plub.plubserver.domain.account.exception;

import lombok.Getter;

@Getter
public enum AccountError {
    // Auth
    FILTER_ACCESS_DENIED(401, "FILTER-001", "access denied."),
    FILTER_ROLE_FORBIDDEN(403, "FILTER-002", "role forbidden."),

    APPLE_LOGIN_ERROR(400, "APPLE-001", "apple login error."),
    SIGNUP_TOKEN_ERROR(400, "ACCOUNT-006", "invalid sign up token error."),

    NOT_FOUND_REFRESH_TOKEN(404, "TOKEN-001", "not found refresh token."),


    // Account
    NOT_FOUND_ACCOUNT(404, "ACCOUNT-001", "not found account error."),
    NICKNAME_DUPLICATION(400, "ACCOUNT-002", "duplicated nickname error."),
    EMAIL_DUPLICATION(400, "ACCOUNT-003", "duplicated email error."),
    NICKNAME_RULE_ERROR(400, "ACCOUNT-004", "invalid nickname rule error."),
    SOCIAL_TYPE_ERROR(400, "ACCOUNT-005", "invalid social type error.");


    private final int HttpCode;
    private final String statusCode;
    private final String message;

    AccountError (int HttpCode, String statusCode, String message) {
        this.HttpCode = HttpCode;
        this.statusCode = statusCode;
        this.message = message;
    }
}
