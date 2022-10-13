package plub.plubserver.domain.account.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import plub.plubserver.domain.account.AccountTemplate;
import plub.plubserver.domain.account.dto.AccountDto;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.exception.account.NotFoundAccountException;
import plub.plubserver.util.s3.AwsS3Uploader;

import static org.assertj.core.api.Assertions.assertThat;
import static plub.plubserver.domain.account.AccountTemplate.*;

@SpringBootTest
@WithMockUser(value = EMAIL,password = PASSWORD)
class AccountServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AwsS3Uploader awsS3Uploader;

    @Autowired
    private AccountService accountService;

    @Test
    void getMyAccount() {
        accountRepository.save(AccountTemplate.makeAccount1());
        AccountDto.AccountInfoResponse myAccount = accountService.getMyAccount();
        assertThat(myAccount.email()).isEqualTo(AccountTemplate.EMAIL);
    }

    @Test
    void getAccount_성공() {
        accountRepository.save(AccountTemplate.makeAccount2());
        AccountDto.AccountInfoResponse findAccount = accountService.getAccount(NICKNAME);
        assertThat(findAccount.email()).isEqualTo(AccountTemplate.EMAIL2);
    }

    @Test
    void getAccount_실패() {
        accountRepository.save(AccountTemplate.makeAccount2());
        Assertions.assertThrows(NotFoundAccountException.class, () -> accountService.getAccount(NICKNAME2));
    }

    @Test @DisplayName("프로필 업데이트 - 성공")
    void updateSuccess() {
        // given
        accountRepository.save(AccountTemplate.makeAccount1());
        MockMultipartFile mockFile = new MockMultipartFile(
                "test",
                "test.png",
                MediaType.IMAGE_JPEG_VALUE,
                "test".getBytes()
        );
        AccountDto.AccountProfileRequest form =
                new AccountDto.AccountProfileRequest("변경된닉네임", "변경된인사", mockFile);

        // when
        AccountDto.AccountInfoResponse accountInfoResponse = accountService.updateProfile(form);

        // then
    }
}