package plub.plubserver.exception.account;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class AuthException extends RuntimeException {
    private final ErrorCode errorCode;

    public AuthException(){
        super("Auth 오류");
        this.errorCode = ErrorCode.AUTH_ERROR;
    }
}