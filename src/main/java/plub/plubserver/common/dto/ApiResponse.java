package plub.plubserver.common.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import plub.plubserver.exception.ErrorCode;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class ApiResponse<T> {

    private static final String SUCCESS_STATUS = "success";
    private static final String FAIL_STATUS = "fail";
    private static final String ERROR_STATUS = "error";

    private String status;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS_STATUS, data, null);
    }
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(SUCCESS_STATUS, data, message);
    }

    public static ApiResponse<?> success() {
        return new ApiResponse<>(SUCCESS_STATUS, null, null);
    }

    // 유효성 검사 실패 -> BindException 에 BindingResult 값이 담겨 있음
    public static ApiResponse<?> fail(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ApiResponse<>(ErrorCode.INVALID_INPUT_VALUE.getCode(), errors, null);
    }

    // 예외 발생
    public static ApiResponse<?> error(String errorCode, String message) {
        return new ApiResponse<>(errorCode, null, message);
    }

    private ApiResponse(String status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }
}