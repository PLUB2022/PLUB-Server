package plub.plubserver.domain.plubbing.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plub.plubserver.common.dto.ApiResponse;

@Slf4j
@RestControllerAdvice
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PlubbingExceptionHandler {
    @ExceptionHandler(PlubbingException.class)
    public ApiResponse<?> handle(PlubbingException ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.plubbingCode.getStatusCode(), ex.getMessage());
    }
}
