package plub.plubserver.domain.account.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import plub.plubserver.domain.account.AccountTemplate;
import plub.plubserver.domain.account.dto.AccountDto;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.exception.AccountException;

import static org.assertj.core.api.Assertions.assertThat;
import static plub.plubserver.domain.account.AccountTemplate.*;

@SpringBootTest
@WithMockUser(value = EMAIL,password = PASSWORD)
class AccountServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;

    @Test
    void getMyAccount() {
        accountRepository.save(AccountTemplate.makeAccount1());
        AccountDto.AccountInfo myAccount = accountService.getMyAccount();
        assertThat(myAccount.email()).isEqualTo(AccountTemplate.EMAIL);
    }

    @Test
    void getAccount_성공() {
        accountRepository.save(AccountTemplate.makeAccount2());
        AccountDto.AccountInfo findAccount = accountService.getAccount(NICKNAME);
        assertThat(findAccount.email()).isEqualTo(AccountTemplate.EMAIL2);
    }

    @Test
    void getAccount_실패() {
        accountRepository.save(AccountTemplate.makeAccount2());
        Assertions.assertThrows(AccountException.class, () -> accountService.getAccount(NICKNAME2));
    }
}