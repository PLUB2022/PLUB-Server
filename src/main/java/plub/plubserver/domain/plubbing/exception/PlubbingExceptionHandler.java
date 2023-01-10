package plub.plubserver.domain.plubbing.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plub.plubserver.common.dto.ApiResponse;

@Slf4j
@RestControllerAdvice
public class PlubbingExceptionHandler {
    @ExceptionHandler(PlubbingException.class)
    public ResponseEntity<?> handle(PlubbingException ex) {
        log.warn("{}({}) - {}", ex.getClass().getSimpleName(), ex.plubbingCode.getStatusCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.plubbingCode.getHttpCode())
                .body(ApiResponse.error(ex.plubbingCode.getStatusCode(), ex.plubbingCode.getMessage()));
    }
}
