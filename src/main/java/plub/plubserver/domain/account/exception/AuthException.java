package plub.plubserver.domain.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import plub.plubserver.domain.account.config.AuthCode;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AuthException extends RuntimeException{
    AuthCode authError;

    public AuthException(AuthCode authError) {
        super(authError.getMessage());
        this.authError = authError;

    }
}
