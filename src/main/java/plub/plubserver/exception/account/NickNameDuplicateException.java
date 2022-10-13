package plub.plubserver.exception.account;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class NicknameDuplicateException extends RuntimeException {
    private final ErrorCode errorCode;

    public NicknameDuplicateException(){
        super("닉네임이 중복되었습니다.");
        this.errorCode = ErrorCode.NICKNAME_DUPLICATION;
    }
}
