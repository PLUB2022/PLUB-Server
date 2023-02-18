package plub.plubserver.common.exception;

public class PlubException extends RuntimeException {
    public StatusCode statusCode;
    public PlubException(StatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
    }

    public PlubException(StatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
