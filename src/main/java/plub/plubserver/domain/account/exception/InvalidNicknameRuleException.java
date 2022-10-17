package plub.plubserver.domain.account.exception;

import lombok.Getter;
import plub.plubserver.exception.ErrorCode;


@Getter
public class InvalidNicknameRuleException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidNicknameRuleException(String nickname) {
        super("[" + nickname + "] " + "닉네임 규칙이 맞지 않습니다. 규칙은 다음 정규식에 알맞아야 합니다. \"^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$\"");
        this.errorCode = ErrorCode.NICKNAME_RULE_ERROR;
    }
}