package plub.plubserver.domain.category.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import plub.plubserver.common.dto.ApiResponse;

@Slf4j
@RestControllerAdvice
public class CategoryExceptionHandler {
    @ExceptionHandler(CategoryException.class)
    public ResponseEntity<ApiResponse<?>> handle(CategoryException ex) {
        log.warn("예외 발생 및 처리 = {} : {}", ex.getClass().getName(), ex.getMessage());
        return ResponseEntity
                .status(ex.categoryError.getHttpCode())
                .body(ApiResponse.error(ex.categoryError.getStatusCode(), ex.categoryError.getMessage()));
    }
}
