package plub.plubserver.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode {
    /**
     * fail
     */
    COMMON_BAD_REQUEST(400, 9000, ""),
    INVALID_INPUT_VALUE(400, 9010, "invalid input value."),
    METHOD_NOT_ALLOWED(405, 9020, "method not allowed."),
    INTERNAL_SERVER_ERROR(500, 9030, "server error."),
    HTTP_CLIENT_ERROR(400, 9040, "http client error."),
    AWS_S3_ERROR(400, 9050, "AWS S3 upload fail."),
    AWS_S3_FILE_SIZE_EXCEEDED(400, 9060, "exceeded file size");



    private final int HttpCode;
    private final int statusCode;
    private final String message;
}
