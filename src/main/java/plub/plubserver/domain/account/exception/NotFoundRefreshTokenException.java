package plub.plubserver.domain.account.exception;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class NotFoundRefreshTokenException extends RuntimeException {
    private final ErrorCode errorCode;

    public NotFoundRefreshTokenException(){
        super("리프레시 토큰 정보를 찾을 수 없습니다.");
        this.errorCode = ErrorCode.NOT_FOUND_REFRESH_TOKEN;
    }
}
