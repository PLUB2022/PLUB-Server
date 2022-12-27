package plub.plubserver.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import plub.plubserver.common.exception.CommonErrorCode;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class ApiResponse<T> {
    final static int SUCCESS_STATUS_CODE = 1000;
    private int statusCode;
    private T data;
    private String message;

    public static <T> ApiResponse<T> success(int statusCode, T data) {
        return new ApiResponse<>(statusCode, data, null);
    }

    public static <T> ApiResponse<T> success(int statusCode, T data, String message) {
        return new ApiResponse<>(statusCode, data, message);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(SUCCESS_STATUS_CODE, data, message);
    }

    public static ApiResponse<?> success(int statusCode) {
        return new ApiResponse<>(statusCode, null, null);
    }

    // 유효성 검사 실패 -> BindException 에 BindingResult 값이 담겨 있음
    public static ApiResponse<?> fail(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ApiResponse<>(CommonErrorCode.INVALID_INPUT_VALUE.getStatusCode(), errors, null);
    }

    // 예외 발생
    public static ApiResponse<?> error(int errorCode, String message) {
        return new ApiResponse<>(errorCode, null, message);
    }

    private ApiResponse(int status, T data, String message) {
        this.statusCode = status;
        this.data = data;
        this.message = message;
    }
}