package plub.plubserver.domain.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import plub.plubserver.domain.account.config.AccountCode;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AccountException extends RuntimeException{
    AccountCode accountError;

    public AccountException(AccountCode accountError) {
        super(accountError.getMessage());
        this.accountError = accountError;
    }
}
