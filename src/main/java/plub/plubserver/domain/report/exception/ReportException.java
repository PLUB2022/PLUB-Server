package plub.plubserver.domain.report.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class ReportException extends PlubException {
    public ReportException(StatusCode statusCode) {
        super(statusCode);
    }
}
