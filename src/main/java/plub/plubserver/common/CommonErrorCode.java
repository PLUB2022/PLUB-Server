package plub.plubserver.common;

import lombok.Getter;

@Getter
public enum CommonErrorCode {
    /**
     * fail
     */
    COMMON_BAD_REQUEST(400, 9000, ""),
    INVALID_INPUT_VALUE(400, 9010, "invalid input value."),
    METHOD_NOT_ALLOWED(405, 9020, "method not allowed."),
    INTERNAL_SERVER_ERROR(500, 9030, "server error."),
    HTTP_CLIENT_ERROR(400, 9040, "http client error."),
    AWS_S3_ERROR(400, 9050, "AWS S3 upload fail.");


    private final int statusCode;
    private final String message;
    private final int HttpCode;

    CommonErrorCode(int HttpCode, int statusCode, String message) {
        this.HttpCode = HttpCode;
        this.message = message;
        this.statusCode = statusCode;
    }
}
