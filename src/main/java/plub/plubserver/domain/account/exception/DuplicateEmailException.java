package plub.plubserver.domain.account.exception;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class DuplicateEmailException extends RuntimeException {
    private final ErrorCode errorCode;

    public DuplicateEmailException(){
        super("이메일이 중복 되었습니다.");
        this.errorCode = ErrorCode.EMAIL_DUPLICATION;
    }
}
