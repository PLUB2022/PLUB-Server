package plub.plubserver.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plub.plubserver.common.dto.ApiResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ApiResponse<?> errorHandle(Exception ex) {
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getMessage());
    }

}
