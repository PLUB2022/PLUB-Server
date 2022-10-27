package plub.plubserver.domain.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AccountException extends RuntimeException{
    AccountError accountError;

    public AccountException(AccountError accountError) {
        super(accountError.getMessage());
        this.accountError = accountError;
    }
}
