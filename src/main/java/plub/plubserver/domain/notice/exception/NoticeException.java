package plub.plubserver.domain.notice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import plub.plubserver.domain.notice.config.NoticeCode;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoticeException extends RuntimeException {
    public NoticeCode noticeCode;

    public NoticeException(NoticeCode noticeCode) {
        super(noticeCode.getMessage());
        this.noticeCode = noticeCode;
    }
}
