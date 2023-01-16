package plub.plubserver.domain.account.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plub.plubserver.common.dto.ApiResponse;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<?> handle(AuthException ex) {
        log.warn("{}({}) - {}", ex.getClass().getSimpleName(), ex.authError.getStatusCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.authError.getHttpCode())
                .body(ApiResponse.error(ex.authError.getStatusCode(), ex.data, ex.authError.getMessage() + ex.message));
    }
}
