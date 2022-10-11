package plub.plubserver.exception.account;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class AppleException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppleException(){
        super("애플 로그인 오류.");
        this.errorCode = ErrorCode.APPLE_LOGIN_ERROR;
    }
}