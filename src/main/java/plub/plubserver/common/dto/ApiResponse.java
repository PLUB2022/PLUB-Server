package plub.plubserver.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private Integer statusCode;
    private T data;
    private String message;

    private ApiResponse(T data) {
        this.statusCode = null;
        this.data = data;
    }

    private ApiResponse(int status, T data, String message) {
        this.statusCode = status;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    public static ApiResponse error(int errorCode, String message) {
        HashMap<String, String> empty = new HashMap<>();
        return new ApiResponse<>(errorCode, empty, message);
    }

    public static <T> ApiResponse<T> error(int errorCode, T data, String message) {
        return new ApiResponse<>(errorCode, data, message);
    }

}