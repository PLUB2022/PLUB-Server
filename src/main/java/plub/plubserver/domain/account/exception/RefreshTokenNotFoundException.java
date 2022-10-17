package plub.plubserver.domain.account.exception;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class RefreshTokenNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    public RefreshTokenNotFoundException(){
        super("리프레시 토큰 정보를 찾을 수 없습니다.");
        this.errorCode = ErrorCode.NOT_FOUND_REFRESH_TOKEN;
    }
}
