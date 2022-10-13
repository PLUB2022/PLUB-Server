package plub.plubserver.exception.account;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;


@Getter
public class InvalidNicknameRuleException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidNicknameRuleException(){
        super("닉네임 규칙이 맞지 않습니다.");
        this.errorCode = ErrorCode.NICKNAME_RULE_ERROR;
    }
}