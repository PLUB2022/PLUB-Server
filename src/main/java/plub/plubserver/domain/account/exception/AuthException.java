package plub.plubserver.domain.account.exception;

import plub.plubserver.domain.account.config.AuthCode;

public class AuthException extends RuntimeException {
    AuthCode authError;

    public AuthException(AuthCode authError) {
        super(authError.getMessage());
        this.authError = authError;

    }
}
