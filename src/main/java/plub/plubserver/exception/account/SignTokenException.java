package plub.plubserver.exception.account;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class SignTokenException extends RuntimeException {
    private final ErrorCode errorCode;

    public SignTokenException(){
        super("회원 가입 정보를 찾을 수 없습니다.");
        this.errorCode = ErrorCode.NOT_FOUND_REFRESH_TOKEN;
    }
}
