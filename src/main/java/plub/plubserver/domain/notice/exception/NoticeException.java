package plub.plubserver.domain.notice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import plub.plubserver.domain.feed.config.FeedCode;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoticeException extends RuntimeException {
    FeedCode feedCode;

    public NoticeException(FeedCode feedCode) {
        super(feedCode.getMessage());
        this.feedCode = feedCode;
    }
}
