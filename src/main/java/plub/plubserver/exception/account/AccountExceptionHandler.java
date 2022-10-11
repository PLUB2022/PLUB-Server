package plub.plubserver.exception.account;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plub.plubserver.common.dto.ApiResponse;

@Slf4j
@RestControllerAdvice
public class AccountExceptionHandler {

    @ExceptionHandler(NickNameDuplicateException.class)
    public ApiResponse<?> handle(NickNameDuplicateException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(EmailDuplicateException.class)
    public ApiResponse<?> handle(EmailDuplicateException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(NotFoundAccountException.class)
    public ApiResponse<?> handle(NotFoundAccountException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(AppleException.class)
    public ApiResponse<?> handle(AppleException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }
}
