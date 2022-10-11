package plub.plubserver.exception.account;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;


@Getter
public class NicknameRuleException extends RuntimeException {
    private final ErrorCode errorCode;

    public NicknameRuleException(){
        super("닉네임 규칙이 맞지 않습니다.");
        this.errorCode = ErrorCode.NICKNAME_RULE_ERROR;
    }
}