package plub.plubserver.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Common
    COMMON_BAD_REQUEST(400, "COMMON-001", ""),
    INVALID_INPUT_VALUE(400, "COMMON-002", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "COMMON-003", "Invalid Input Value"),
    INTERNAL_SERVER_ERROR(500, "COMMON-004", "Server Error"),
    HTTP_CLIENT_ERROR(400, "COMMON-005", "Http Client Error");

//    // Filter
//    FILTER_ACCESS_DENIED(401, "FILTER-001", "Access is Denied"),
//    FILTER_ROLE_FORBIDDEN(403, "FILTER-002", "Role Forbidden"),

    // Account
//    NOT_FOUND_ACCOUNT(404, "ACCOUNT-001", "Not Fount Account"),
//    NICKNAME_DUPLICATION(400, "ACCOUNT-002", "Nickname is Duplication"),
//    EMAIL_DUPLICATION(400, "ACCOUNT-003", "Email is Duplication"),
//    NICKNAME_RULE_ERROR(400, "ACCOUNT-004", "Nickname Rule Error"),
//    SOCIAL_TYPE_ERROR(400, "ACCOUNT-005", "SocialType Error"),
//    SIGNUP_ERROR(400, "ACCOUNT-006", "SignUp Error"),
//
//    APPLE_LOGIN_ERROR(400, "APPLE-001", "Apple Login Error"),
//    AUTH_ERROR(400, "AUTH-001", "Auth Error"),
//
//    // Token
//    NOT_FOUND_REFRESH_TOKEN(404, "TOKEN-001", "Not Fount RefreshToken");

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
