package plub.plubserver.domain.account.exception;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class SignTokenException extends RuntimeException {
    private final ErrorCode errorCode;

    public SignTokenException(String rawToken){
        super("[" + rawToken + "]\n토큰 헤더 정보가 잘못 되었습니다.");
        this.errorCode = ErrorCode.NOT_FOUND_REFRESH_TOKEN;
    }
}
