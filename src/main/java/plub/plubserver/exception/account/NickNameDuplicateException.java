package plub.plubserver.exception.account;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class NickNameDuplicateException extends RuntimeException {
    private final ErrorCode errorCode;

    public NickNameDuplicateException(){
        super("닉네임이 중복되었습니다.");
        this.errorCode = ErrorCode.NICKNAME_DUPLICATION;
    }
}
