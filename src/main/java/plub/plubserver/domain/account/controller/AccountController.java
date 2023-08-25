package plub.plubserver.domain.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import plub.plubserver.common.dto.ApiResponse;
import plub.plubserver.domain.account.dto.AuthDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.service.AccountService;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static plub.plubserver.common.dto.ApiResponse.success;
import static plub.plubserver.domain.account.dto.AccountDto.*;
import static plub.plubserver.domain.account.dto.AuthDto.*;
import static plub.plubserver.domain.account.dto.AuthDto.AuthMessage;


@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Api(tags = "회원 API", hidden = true)
public class AccountController {
    private final AccountService accountService;

    @ApiOperation(value = "회원 정보")
    @GetMapping("/me")
    public ApiResponse<AccountInfoResponse> getMyAccountInfo() {
        return success(accountService.getMyAccount());
    }

    @ApiOperation(value = "닉네임으로 회원 조회")
    @GetMapping("/{nickname}")
    public ApiResponse<AccountInfoResponse> getAccountInfo(@PathVariable String nickname) {
        return success(accountService.getAccount(nickname));
    }

    @ApiOperation(value = "닉네임 검증 API")
    @GetMapping("/check/nickname/{nickname}")
    public ApiResponse<NicknameResponse> isDuplicateNickname(@PathVariable String nickname) {
        return success(accountService.isDuplicateNickname(nickname));
    }

    @ApiOperation(value = "회원 프로필 수정 (프로필 사진, 인사말, 닉네임)")
    @PostMapping("/me/profile")
    public ApiResponse<AccountInfoResponse> updateProfile(
            @Valid @RequestBody AccountProfileRequest accountProfileRequest
    ) {
        return success(accountService.updateProfile(accountProfileRequest));
    }

    @ApiOperation(value = "회원 앱 전체 푸시알림 수신 여부 변경")
    @PutMapping("/me")
    public ApiResponse<AccountPushNotificationStatusResponse> updatePushNotificationStatus(
            @RequestParam(value = "push-notification", defaultValue = "true") boolean pushNotificationStatus
    ) {
        return success(accountService.updatePushNotificationStatus(pushNotificationStatus));
    }

    @ApiOperation(value = "회원 탈퇴")
    @PostMapping("/revoke")
    public ApiResponse<RevokeResponse> revoke() {
        RevokeResponse revoke = accountService.revoke();
        return success(revoke);
    }

    @ApiOperation(value = "회원 관심사 등록")
    @PostMapping("/interest")
    public ApiResponse<AccountCategoryResponse> createAccountCategory(
            @Valid @RequestBody AccountCategoryRequest accountCategoryRequest
    ) {
        return success(accountService.createAccountCategory(accountCategoryRequest));
    }

    @ApiOperation(value = "회원 관심사 조회")
    @GetMapping("/interest")
    public ApiResponse<AccountCategoryResponse> getAccountCategory() {
        return success(accountService.getAccountCategory());
    }

    @ApiOperation(value = "회원 전체 조회")
    @GetMapping("")
    public ApiResponse<AccountListResponse> getAccountList(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return success(accountService.getAccountList(pageable));
    }

    @ApiOperation(value = "회원 검색")
    @GetMapping("/search")
    public ApiResponse<AccountListResponse> searchAccountList(
            @RequestParam String keyword,
            @RequestParam(required = false) String startedAt,
            @RequestParam(required = false) String endedAt,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return success(accountService.searchAccountList(startedAt, endedAt, keyword, pageable));
    }

    @ApiOperation(value = "회원 상태 변경")
    @PutMapping("/{accountId}/update-status")
    public ApiResponse<AccountIdResponse> updateAccountStatus(
            @PathVariable Long accountId,
            @RequestParam String status
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(accountService.updateAccountStatus(currentAccount, accountId, status));
    }

    @ApiOperation(value = "회원 영구 정지 해제")
    @PutMapping("/{accountId}/unsuspend")
    public ApiResponse<AccountIdResponse> unSuspendAccount(
            @PathVariable Long accountId
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(accountService.unSuspendAccount(currentAccount, accountId));
    }

    @ApiOperation(value = "sms 인증번호 전송")
    @PostMapping("/sms")
    public ApiResponse<SmsResponse> sendSms(@RequestBody SmsRequest smsRequest) throws UnsupportedEncodingException, URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        SmsResponse smsResponse = accountService.sendSms(smsRequest);
        return success(smsResponse);
    }

    @ApiOperation(value = "sms 인증번호 확인")
    @PostMapping("/sms/certification")
    public ApiResponse<SmsMessage> certifySms(@RequestBody CertifySmsRequest certifySmsRequest){
        SmsMessage smsMessage = accountService.certifySms(certifySmsRequest);
        return success(smsMessage);
    }

    @ApiOperation(value = "회원 비활성화 설정/해제")
    @PutMapping("/inactive")
    public ApiResponse<AccountIdResponse> inActiveAccount(
            @RequestParam(value = "inactive", defaultValue = "true") boolean isInactive
    ) {
        Account currentAccount = accountService.getCurrentAccount();
        return success(accountService.inActiveAccount(currentAccount, isInactive));
    }
}


