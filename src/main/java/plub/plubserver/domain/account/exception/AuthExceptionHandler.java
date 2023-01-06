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
        log.warn("{}({}) - {}", ex.getClass().getSimpleName(), ex.authError.getStatusCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.authError.getHttpCode())
                .body(ApiResponse.error(ex.authError.getStatusCode(), ex.data, ex.authError.getMessage()));
    }
}
