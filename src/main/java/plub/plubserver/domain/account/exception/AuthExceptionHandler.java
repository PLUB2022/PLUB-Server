package plub.plubserver.domain.account.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plub.plubserver.common.dto.ApiResponse;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<?>> handle(AuthException ex) {
        log.warn("로그인 예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ResponseEntity
                .status(ex.authError.getHttpCode())
                .body(ApiResponse.error(ex.authError.getStatusCode(), ex.authError.getMessage()));
    }
}
