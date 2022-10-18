package plub.plubserver.domain.account.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plub.plubserver.common.dto.ApiResponse;

@Slf4j
@RestControllerAdvice
public class AccountExceptionHandler {

    @ExceptionHandler(NotFoundAccountException.class)
    public ApiResponse<?> handle(NotFoundAccountException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(AppleLoginException.class)
    public ApiResponse<?> handle(AppleLoginException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(DuplicateNicknameException.class)
    public ApiResponse<?> handle(DuplicateNicknameException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ApiResponse<?> handle(DuplicateEmailException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(InvalidSocialTypeException.class)
    public ApiResponse<?> handle(InvalidSocialTypeException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(InvalidNicknameRuleException.class)
    public ApiResponse<?> handle(InvalidNicknameRuleException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(NotFoundRefreshTokenException.class)
    public ApiResponse<?> handle(NotFoundRefreshTokenException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(SignTokenException.class)
    public ApiResponse<?> handle(SignTokenException ex){
        log.error("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getMessage());
    }
}
