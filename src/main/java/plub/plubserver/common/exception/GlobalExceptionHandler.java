package plub.plubserver.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.util.s3.AwsS3Exception;

import javax.validation.ValidationException;
import java.io.IOException;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<?> errorHandle(IOException ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(CommonErrorCode.INVALID_INPUT_VALUE.getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    protected ApiResponse<?> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getMessage(), ex.getMessage());
        return ApiResponse.error(CommonErrorCode.METHOD_NOT_ALLOWED.getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<?> httpClientErrorException(HttpClientErrorException ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(CommonErrorCode.HTTP_CLIENT_ERROR.getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> validationException(final ValidationException ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getMessage(), ex.getMessage());
        return ApiResponse.error(CommonErrorCode.INVALID_INPUT_VALUE.getStatusCode(), ex.getMessage());
    }

    // @Valid 실패시 이 예외가 터져서 잡아줘야 함
    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> validationException(final BindException ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getMessage(), ex.getMessage());
        return ApiResponse.error(CommonErrorCode.INVALID_INPUT_VALUE.getStatusCode(), ex.getMessage());
    }

    // Aws S3 Error
    @ExceptionHandler(AwsS3Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> awsS3Error(final AwsS3Exception ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getMessage(), ex.getMessage());
        return ApiResponse.error(CommonErrorCode.AWS_S3_ERROR.getHttpCode(), ex.getMessage());
    }

    // DB Entity Not found
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected ApiResponse<?> notFoundError(final NoSuchElementException ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getMessage(), ex.getMessage());
        return ApiResponse.error(CommonErrorCode.AWS_S3_ERROR.getHttpCode(), ex.getMessage());
    }

}
