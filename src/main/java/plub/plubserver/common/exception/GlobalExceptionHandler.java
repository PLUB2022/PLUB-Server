package plub.plubserver.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
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

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<?> globalHandle(Exception ex) {
        log.warn("글로벌 {} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return error(COMMON_BAD_REQUEST.getStatusCode(), ex.getMessage());
    }

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

    // @Valid 실패시 이 예외가 터져서 잡아줘야 함
    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> validationException(final BindException ex) {
        log.warn("ValidationException({}) - {}", ex.getClass().getSimpleName(), ex.getMessage());
        StringBuilder reason = new StringBuilder();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            reason.append(fieldError.getDefaultMessage()).append(",");
        }
        return error(INVALID_INPUT_VALUE.getStatusCode(), reason.toString());
    }

    // Aws S3 Error
    @ExceptionHandler(AwsS3Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> awsS3Error(final AwsS3Exception ex) {
        log.warn("{} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return error(AWS_S3_UPLOAD_FAIL.getStatusCode(), ex.getMessage());
    }

    // 파일 업로드 용량 초과시 발생
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> handleMaxUploadSizeException(final MaxUploadSizeExceededException ex) {
        log.warn("{} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return error(AWS_S3_FILE_SIZE_EXCEEDED.getStatusCode(), AWS_S3_FILE_SIZE_EXCEEDED.getMessage());
    }

    // LocalDateTime 파싱 에러
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex) {
        log.warn("{} - {}", ex.getClass().getSimpleName(), ex.getMessage());
        return error(INVALID_INPUT_VALUE.getStatusCode(), INVALID_INPUT_VALUE.getMessage());
    }
}
