package plub.plubserver.domain.recruit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plub.plubserver.common.dto.ApiResponse;

@Slf4j
@RestControllerAdvice
public class RecruitExceptionHandler {
    @ExceptionHandler(RecruitException.class)
    public ResponseEntity<ApiResponse<?>> handle(RecruitException ex) {
        log.warn("{}({}) - {}", ex.getClass().getSimpleName(), ex.recruitCode.getStatusCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.recruitCode.getHttpCode())
                .body(ApiResponse.error(ex.recruitCode.getStatusCode(), ex.recruitCode.getMessage()));
    }
}
