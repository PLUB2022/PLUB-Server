package plub.plubserver.domain.account.config;

import lombok.Getter;

@Getter
public enum AccountCode {
    /**
     * success
     */
    ACCOUNT_SUCCESS(200, 1001, "account request complete."),
    NEED_TO_SIGNUP(200, 1011, "need to signup, X-ACCESS-TOKEN is issued."),
    LOGIN(200, 1021, "account exist, process login."),
    SIGNUP_COMPLETE(200, 1031, "signup complete, access token is issued."),


    /**
     * fail
     */
    // Auth
    FILTER_ACCESS_DENIED(401, 1000, "access denied."),
    FILTER_ROLE_FORBIDDEN(403, 1010, "role forbidden."),

    APPLE_LOGIN_ERROR(400, 1020, "apple login error."),
    SIGNUP_TOKEN_ERROR(400, 1030, "invalid sign up token error."),

    NOT_FOUND_REFRESH_TOKEN(404, 1040, "not found refresh token."),


    // Account
    NOT_FOUND_ACCOUNT(404, 1050, "not found account error."),
    NICKNAME_DUPLICATION(400, 1060, "duplicated nickname error."),
    EMAIL_DUPLICATION(400, 1070, "duplicated email error."),
    NICKNAME_RULE_ERROR(400, 1080, "invalid nickname rule error."),
    SOCIAL_TYPE_ERROR(400, 1090, "invalid social type error.");

    private final int HttpCode;
    private final int statusCode;
    private final String message;

    AccountCode(int HttpCode, int statusCode, String message) {
        this.HttpCode = HttpCode;
        this.statusCode = statusCode;
        this.message = message;
    }
}
