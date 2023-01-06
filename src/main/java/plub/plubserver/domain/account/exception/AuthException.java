package plub.plubserver.domain.account.exception;

import plub.plubserver.domain.account.config.AuthCode;

public class AuthException extends RuntimeException {
    AuthCode authError;
    Object data;

    public AuthException(AuthCode authError) {
        super(authError.getMessage());
        this.authError = authError;

    }

    public AuthException(Object data, AuthCode authError) {
        super(authError.getMessage());
        this.data = data;
        this.authError = authError;

    }
}
