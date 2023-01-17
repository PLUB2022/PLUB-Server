package plub.plubserver.domain.account.exception;

import plub.plubserver.domain.account.config.AuthCode;

import java.util.HashMap;

public class AuthException extends RuntimeException {
    AuthCode authError;
    Object data;
    String message;

    public AuthException(AuthCode authError) {
        super(authError.getMessage());
        this.authError = authError;
        this.message = authError.getMessage();
        this.data = new HashMap<String, String>();
    }

    public AuthException(AuthCode authError, String message) {
        super(authError.getMessage());
        this.authError = authError;
        this.message = message;
        this.data = new HashMap<String, String>();
    }

    public AuthException(Object data, AuthCode authError) {
        super(authError.getMessage());
        this.data = data;
        this.authError = authError;
        this.message = authError.getMessage();
    }

}
