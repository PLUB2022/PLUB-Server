package plub.plubserver.domain.account.exception;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;

@Getter
public class DuplicateNicknameException extends RuntimeException {
    private final ErrorCode errorCode;

    public DuplicateNicknameException(){
        super("닉네임이 중복 되었습니다.");
        this.errorCode = ErrorCode.NICKNAME_DUPLICATION;
    }
}
