package plub.plubserver.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import plub.plubserver.common.dto.ApiResponse;

import javax.validation.ValidationException;
import java.io.IOException;

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
    public ApiResponse<?> HttpClientErrorException(HttpClientErrorException ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(CommonErrorCode.HTTP_CLIENT_ERROR.getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> ValidationException(final ValidationException ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getMessage(), ex.getMessage());
        return ApiResponse.error(CommonErrorCode.INVALID_INPUT_VALUE.getStatusCode(), ex.getMessage());
    }

    // @Valid 실패시 이 예외가 터져서 잡아줘야 함
    @ExceptionHandler(BindException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    protected ApiResponse<?> ValidationException(final BindException ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getMessage(), ex.getMessage());
        return ApiResponse.error(CommonErrorCode.INVALID_INPUT_VALUE.getStatusCode(), ex.getMessage());
    }

}
