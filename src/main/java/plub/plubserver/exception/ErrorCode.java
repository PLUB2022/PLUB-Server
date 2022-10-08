package plub.plubserver.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Common
    COMMON_BAD_REQUEST(400, "COMMON-001", ""),
    INVALID_INPUT_VALUE(400, "COMMON-002", "Invalid Input Value"),

    // Filter
    FILTER_ACCESS_DENIED(401, "FILTER-001", "Access is Denied"),
    FILTER_ROLE_FORBIDDEN(403, "FILTER-002", "Role Forbidden"),

    // Account
    NOT_FOUND_ACCOUNT(404, "ACCOUNT-001", "Not Fount Account"),
    NICKNAME_DUPLICATION(400, "ACCOUNT-002", "Nickname is Duplication"),
    EMAIL_DUPLICATION(400, "ACCOUNT-003", "Email is Duplication"),
    APPLE_LOGIN_ERROR(400, "APPLE-001", "Apple Login Error");

    private final String code;
    private final String message;
    private int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
