package plub.plubserver.domain.account.exception;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class SignTokenException extends RuntimeException {
    private final ErrorCode errorCode;

    public SignTokenException(String rawToken){
        super("[" + rawToken + "]\n회원 가입 토큰에 오류가 있습니다.");
        this.errorCode = ErrorCode.NOT_FOUND_REFRESH_TOKEN;
    }
}
