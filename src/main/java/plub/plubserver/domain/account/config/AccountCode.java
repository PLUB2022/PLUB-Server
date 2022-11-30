package plub.plubserver.domain.account.config;

import lombok.Getter;

@Getter
public enum AccountCode {
    /**
     * success
     */
    ACCOUNT_SUCCESS(200, 1000, "account request complete."),
    NEED_TO_SIGNUP(200, 2001, "need to signup, X-ACCESS-TOKEN is issued."),
    LOGIN(200, 1000, "account exist, process login."),
    SIGNUP_COMPLETE(200, 1000, "signup complete, access token is issued."),
    ADMIN_LOGIN(200, 1000, "admin check, process login."),


    /**
     * fail
     */
    // Auth
    FILTER_ACCESS_DENIED(401, 2000, "access denied."),
    FILTER_ROLE_FORBIDDEN(403, 2010, "role forbidden."),

    APPLE_LOGIN_ERROR(400, 2020, "apple login error."),
    SIGNUP_TOKEN_ERROR(400, 2030, "invalid sign up token error."),

    NOT_FOUND_REFRESH_TOKEN(404, 2040, "not found refresh token."),
    ENCRYPTION_FAILURE(400, 2100, "encryption failure"),
    DECRYPTION_FAILURE(400, 2110, "decryption failed."),

    // Account
    NOT_FOUND_ACCOUNT(404, 2050, "not found account error."),
    NICKNAME_DUPLICATION(400, 2060, "duplicated nickname error."),
    EMAIL_DUPLICATION(400, 2070, "duplicated email error."),
    NICKNAME_RULE_ERROR(400, 2080, "invalid nickname rule error."),
    SOCIAL_TYPE_ERROR(400, 2090, "invalid social type error."),
    ROLE_ACCESS_ERROR(400, 2120, "role access error.");

    private final int HttpCode;
    private final int statusCode;
    private final String message;

    AccountCode(int HttpCode, int statusCode, String message) {
        this.HttpCode = HttpCode;
        this.statusCode = statusCode;
        this.message = message;
    }
}
