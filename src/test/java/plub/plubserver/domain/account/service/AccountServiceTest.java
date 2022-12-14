//package plub.plubserver.domain.account.service;
//
//import io.findify.s3mock.S3Mock;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.test.context.support.WithMockUser;
//import plub.plubserver.domain.account.AccountTemplate;
//import plub.plubserver.domain.account.dto.AccountDto.AccountProfileRequest;
//import plub.plubserver.domain.account.repository.AccountRepository;
//import plub.plubserver.domain.account.exception.InvalidNicknameRuleException;
//import plub.plubserver.domain.account.exception.NotFoundAccountException;
//import plub.plubserver.util.AwsS3MockConfig;
//import plub.plubserver.util.s3.AwsS3Uploader;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static plub.plubserver.domain.account.AccountTemplate.*;
//import static plub.plubserver.domain.account.dto.AccountDto.AccountInfoResponse;
//
//@SpringBootTest
//@Import(AwsS3MockConfig.class)
//@WithMockUser(value = EMAIL, password = PASSWORD)
//class AccountServiceTest {
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Autowired
//    private S3Mock s3Mock;
//
//    @Autowired
//    private AwsS3Uploader awsS3Uploader;
//
//    @Autowired
//    private AccountService accountService;
//
//    MockMultipartFile mockFile = new MockMultipartFile(
//            "test",
//            "test.png",
//            MediaType.IMAGE_JPEG_VALUE,
//            "test".getBytes()
//    );
//
//    @Test
//    void getMyAccount() {
//        accountRepository.save(AccountTemplate.makeAccount1());
//        AccountInfoResponse myAccount = accountService.getMyAccount();
//        assertThat(myAccount.email()).isEqualTo(AccountTemplate.EMAIL);
//    }
//
//    @Test
//    void getAccount_??????() {
//        accountRepository.save(AccountTemplate.makeAccount2());
//        AccountInfoResponse findAccount = accountService.getAccount(NICKNAME);
//        assertThat(findAccount.email()).isEqualTo(AccountTemplate.EMAIL2);
//    }
//
//    @Test
//    void getAccount_??????() {
//        accountRepository.save(AccountTemplate.makeAccount2());
//        assertThrows(NotFoundAccountException.class, () -> accountService.getAccount(NICKNAME2));
//    }
//
//    @Test @DisplayName("updateProfile ??????")
//    void updateProfileSuccess() {
//        // given
//        accountRepository.save(AccountTemplate.makeAccount1());
//        AccountProfileRequest form =
//                new AccountProfileRequest("??????????????????", "???????????????", mockFile);
//
//        // when
//        AccountInfoResponse expected = accountService.updateProfile(form);
//
//        // then
//        assertThat(expected.nickname()).isEqualTo("??????????????????");
//        assertThat(expected.introduce()).isEqualTo("???????????????");
//        assertThat(expected.profileImage()).contains(mockFile.getOriginalFilename());
//    }
//
//    @Test @DisplayName("updateProfile ?????? - ????????? ????????? ??????")
//    void updateProfileFail1() {
//        accountRepository.save(AccountTemplate.makeAccount1());
//
//        // ???????????? ??????
//        assertThrows(InvalidNicknameRuleException.class, () ->
//                accountService.updateProfile(new AccountProfileRequest("___", "???????????????", mockFile))
//        );
//
//        // ?????? ??????
//        InvalidNicknameRuleException e = assertThrows(InvalidNicknameRuleException.class, () ->
//                accountService.updateProfile(new AccountProfileRequest(" ?????????", "???????????????", mockFile))
//        );
//
//        // TODO : ?????? ?????? ???????????? ????????? ???
//    }
//
//    @Test @DisplayName("updateProfile ?????? - ????????? ????????? null")
//    void updateProfileFail2() {
//        accountRepository.save(AccountTemplate.makeAccount1());
//        AccountProfileRequest form =
//                new AccountProfileRequest("??????????????????", "???????????????", null);
//
//        assertThrows(NullPointerException.class, () ->
//                accountService.updateProfile(form)
//        );
//    }
//}