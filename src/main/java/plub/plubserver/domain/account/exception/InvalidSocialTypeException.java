package plub.plubserver.domain.account.exception;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class InvalidSocialTypeException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidSocialTypeException(String socialType){
        super("[" + socialType + "] 해당 소셜 정보는 지원하지 않습니다.");
        this.errorCode = ErrorCode.NOT_FOUND_ACCOUNT;
    }
}
