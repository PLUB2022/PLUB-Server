package plub.plubserver.exception.account;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class EmailDuplicateException extends RuntimeException {
    private final ErrorCode errorCode;

    public EmailDuplicateException(){
        super("닉네임이 중복되었습니다.");
        this.errorCode = ErrorCode.EMAIL_DUPLICATION;
    }
}
