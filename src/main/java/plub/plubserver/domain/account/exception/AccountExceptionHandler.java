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
public class AccountExceptionHandler {
    @ExceptionHandler(AccountException.class)
    public ResponseEntity<?> handle(AccountException ex) {
        log.warn("{}({}) - {}", ex.getClass().getSimpleName(), ex.accountError.getStatusCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.accountError.getHttpCode())
                .body(ApiResponse.error(ex.accountError.getStatusCode(), ex.accountError.getMessage()));
    }
}
