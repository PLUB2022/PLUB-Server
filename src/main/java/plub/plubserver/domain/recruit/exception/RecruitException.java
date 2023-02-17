package plub.plubserver.domain.recruit.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class RecruitException extends PlubException {

    public RecruitException(StatusCode statusCode) {
        super(statusCode);
    }
}
