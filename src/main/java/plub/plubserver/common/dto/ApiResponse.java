package plub.plubserver.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    final static int SUCCESS_STATUS_CODE = 1000;
    private int statusCode;
    private T data;
    private String message;

    private ApiResponse(int status, T data) {
        this.statusCode = status;
        this.data = data;
    }

    private ApiResponse(int status, String message) {
        this.statusCode = status;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SUCCESS_STATUS_CODE, data);
    }

    public static <T> ApiResponse<T> success(int statusCode, T data) {
        return new ApiResponse<>(statusCode, data);
    }

    public static ApiResponse<?> error(int errorCode, String message) {
        return new ApiResponse<>(errorCode, message);
    }

}