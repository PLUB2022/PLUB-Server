package plub.plubserver.domain.archive.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class ArchiveException extends PlubException {
    public ArchiveException(StatusCode statusCode) {
        super(statusCode);
    }
}
