package plub.plubserver.domain.account.exception;

import plub.plubserver.domain.account.config.AccountCode;

public class AccountException extends RuntimeException {
    public AccountCode accountError;

    public AccountException(AccountCode accountError) {
        super(accountError.getMessage());
        this.accountError = accountError;
    }
}
