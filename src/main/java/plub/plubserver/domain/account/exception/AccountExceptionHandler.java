package plub.plubserver.domain.account.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plub.plubserver.common.dto.ApiResponse;

@Slf4j
@RestControllerAdvice
public class AccountExceptionHandler {
    @ExceptionHandler(AccountException.class)
    public ResponseEntity<ApiResponse<?>> handle(AccountException ex) {
        log.warn("{}({}) - {}", ex.getClass().getSimpleName(), ex.accountError.getStatusCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.accountError.getHttpCode())
                .body(ApiResponse.error(ex.accountError.getStatusCode(), ex.accountError.getMessage()));
    }
}
