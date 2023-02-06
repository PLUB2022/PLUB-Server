package plub.plubserver.domain.todo.exception;

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
public class TodoExceptionHandler {
    @ExceptionHandler(TodoException.class)
    public ResponseEntity<?> handle(TodoException ex) {
        log.warn("{}({}) - {}", ex.getClass().getSimpleName(), ex.todoCode.getHttpCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.todoCode.getHttpCode())
                .body(ApiResponse.error(ex.todoCode.getStatusCode(), ex.todoCode.getMessage()));
    }
}
