package plub.plubserver.domain.category.exception;

import plub.plubserver.common.exception.PlubException;
import plub.plubserver.common.exception.StatusCode;

public class CategoryException extends PlubException {
    public CategoryException(StatusCode statusCode) {
        super(statusCode);
    }
}
