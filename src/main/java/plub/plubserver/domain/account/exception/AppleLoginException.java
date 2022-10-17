package plub.plubserver.domain.account.exception;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class AppleLoginException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppleLoginException(){
        super("애플 로그인 오류.");
        this.errorCode = ErrorCode.APPLE_LOGIN_ERROR;
    }
}