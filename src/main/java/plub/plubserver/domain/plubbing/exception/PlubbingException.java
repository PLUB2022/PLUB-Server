package plub.plubserver.domain.plubbing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import plub.plubserver.domain.plubbing.config.PlubbingCode;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PlubbingException extends RuntimeException {
    public PlubbingCode plubbingCode;

    public PlubbingException(PlubbingCode plubbingCode) {
        super(plubbingCode.getMessage());
        this.plubbingCode = plubbingCode;
    }
}
