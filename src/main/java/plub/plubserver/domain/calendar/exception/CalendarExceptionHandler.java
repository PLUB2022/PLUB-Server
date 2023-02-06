package plub.plubserver.domain.calendar.exception;

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
public class CalendarExceptionHandler {
    @ExceptionHandler(CalendarException.class)
    public ResponseEntity<?> handle(CalendarException ex) {
        log.warn("{}({}) - {}", ex.getClass().getSimpleName(), ex.calendarCode.getStatusCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.calendarCode.getHttpCode())
                .body(ApiResponse.error(ex.calendarCode.getStatusCode(), ex.calendarCode.getMessage()));
    }
}
