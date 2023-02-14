package plub.plubserver.domain.notice.exception;

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
public class NoticeExceptionHandler {
    @ExceptionHandler(NoticeException.class)
    public ResponseEntity<?> handle(NoticeException ex) {
        log.warn("{}({}) - {}", ex.getClass().getSimpleName(), ex.noticeCode.getStatusCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.noticeCode.getHttpCode())
                .body(ApiResponse.error(ex.noticeCode.getStatusCode(), ex.noticeCode.getMessage()));
    }
}
