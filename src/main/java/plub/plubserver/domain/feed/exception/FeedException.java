package plub.plubserver.domain.feed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import plub.plubserver.domain.feed.config.FeedCode;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FeedException extends RuntimeException {
    public FeedCode feedCode;

    public FeedException(FeedCode feedCode) {
        super(feedCode.getMessage());
        this.feedCode = feedCode;
    }
}
