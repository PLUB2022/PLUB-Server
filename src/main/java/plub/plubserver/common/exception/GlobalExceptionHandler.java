package plub.plubserver.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.util.s3.exception.AwsS3Exception;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

import static plub.plubserver.common.dto.ApiResponse.error;
import static plub.plubserver.common.exception.CommonErrorCode.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<?> errorHandle(IOException ex) {
        log.warn("{} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return error(INVALID_INPUT_VALUE.getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    protected ApiResponse<?> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException ex) {
        log.warn("{} - {}", ex.getMessage(), ex.getMessage());
        return error(METHOD_NOT_ALLOWED.getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<?> httpClientErrorException(HttpClientErrorException ex) {
        log.warn("{} - {}", ex.getClass().getName(), ex.getMessage());
        return error(HTTP_CLIENT_ERROR.getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> validationException(final ConstraintViolationException ex) {
        log.warn("{} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return error(INVALID_INPUT_VALUE.getStatusCode(), ex.getConstraintViolations().iterator().next().getMessage());
    }

    // @Valid ????????? ??? ????????? ????????? ???????????? ???
    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> validationException(final BindException ex) {
        log.warn("ValidationException({}) - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return error(INVALID_INPUT_VALUE.getStatusCode(), ex.getMessage());
    }

    // Aws S3 Error
    @ExceptionHandler(AwsS3Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> awsS3Error(final AwsS3Exception ex) {
        log.warn("{} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return error(AWS_S3_ERROR.getStatusCode(), ex.getMessage());
    }

    // ?????? ????????? ?????? ????????? ??????
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> handleMaxUploadSizeException(final MaxUploadSizeExceededException ex) {
        log.warn("{} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return error(AWS_S3_FILE_SIZE_EXCEEDED.getStatusCode(), AWS_S3_FILE_SIZE_EXCEEDED.getMessage());
    }
}
