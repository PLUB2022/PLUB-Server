package plub.plubserver.domain.account.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.config.redis.RedisService;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.*;
import plub.plubserver.domain.account.repository.AccountNicknameHistoryRepository;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.domain.account.repository.SuspendAccountRepository;
import plub.plubserver.domain.category.exception.CategoryException;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.category.repository.SubCategoryRepository;
import plub.plubserver.domain.report.config.ReportStatusMessage;
import plub.plubserver.domain.report.exception.ReportException;
import plub.plubserver.domain.report.service.ReportService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import static plub.plubserver.common.constant.GlobalConstants.NICKNAME_CHANGE_LIMIT;
import static plub.plubserver.config.security.SecurityUtils.getCurrentAccountEmail;
import static plub.plubserver.domain.account.dto.AccountDto.*;
import static plub.plubserver.domain.account.dto.AuthDto.AuthMessage;
import static plub.plubserver.domain.account.model.AccountStatus.NORMAL;
import static plub.plubserver.domain.notification.model.NotificationType.*;
import static plub.plubserver.domain.report.config.ReportMessage.REPORT_TITLE_ADMIN;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    @Value("${naver-cloud-sms.access-key}")
    private String accessKey;

    @Value("${naver-cloud-sms.secret-key}")
    private String secretKey;

    @Value("${naver-cloud-sms.service-id}")
    private String serviceId;

    @Value("${naver-cloud-sms.sender-phone}")
    private String phone;

    private final AccountRepository accountRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final AppleService appleService;
    private final GoogleService googleService;
    private final KakaoService kakaoService;
    private final ReportService reportService;
    private final RedisService redisService;
    private final SuspendAccountRepository suspendAccountRepository;
    private final AccountNicknameHistoryRepository accountNicknameHistoryRepository;

    // 회원 정보 조회
    public AccountInfoResponse getMyAccount() {
        return accountRepository.findByEmail(getCurrentAccountEmail()).map(AccountInfoResponse::of).orElseThrow(() -> new AccountException(StatusCode.NOT_FOUND_ACCOUNT));
    }

    public AccountInfoResponse getAccount(String nickname) {
        return accountRepository.findByNickname(nickname).map(AccountInfoResponse::of).orElseThrow(() -> new AccountException(StatusCode.NOT_FOUND_ACCOUNT));
    }

    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new AccountException(StatusCode.NOT_FOUND_ACCOUNT));
    }

    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(() -> new AccountException(StatusCode.NOT_FOUND_ACCOUNT));
    }

    public Account getCurrentAccount() {
        return accountRepository.findByEmail(getCurrentAccountEmail()).orElseThrow(() -> new AccountException(StatusCode.NOT_FOUND_ACCOUNT));
    }

    public NicknameResponse isDuplicateNickname(String nickname) {
        validateNicknameChangeLimit(getCurrentAccount());
        validateNicknameDuplication(nickname);
        return new NicknameResponse(true);
    }

    public void validateNicknameDuplication(String nickname) {
        String pattern = "^[0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]*$";
        if (!Pattern.matches(pattern, nickname)) {
            throw new AccountException(StatusCode.NICKNAME_ERROR);
        }
        if (accountRepository.existsByNickname(nickname)) {
            throw new AccountException(StatusCode.NICKNAME_DUPLICATION);
        }
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public AccountInfoResponse updateProfile(AccountProfileRequest profileRequest) {
        Account loginAccount = getCurrentAccount();
        String newNickname = profileRequest.nickname();

        if (!loginAccount.getNickname().equals(newNickname)) {
            validateNicknameChangeLimit(loginAccount);
            validateNicknameDuplication(newNickname);
            saveNicknameHistory(loginAccount.getNickname(), loginAccount);
        }

        loginAccount.updateProfile(newNickname, profileRequest.introduce(), profileRequest.profileImageUrl());
        return AccountInfoResponse.of(loginAccount);
    }

    private void validateNicknameChangeLimit(Account account) {
        int nicknameChangeCount = accountNicknameHistoryRepository.countAllByAccount(account);
        if (nicknameChangeCount >= NICKNAME_CHANGE_LIMIT) {
            throw new AccountException(StatusCode.NICKNAME_CHANGE_LIMIT);
        }
    }

    private void saveNicknameHistory(String oldNickname, Account account) {
        AccountNicknameHistory history = AccountNicknameHistory.builder().account(account).nickname(oldNickname).changedAt(LocalDateTime.now()).build();
        accountNicknameHistoryRepository.save(history);
    }

    // 앱 전체 푸시 알림 여부 수정
    @Transactional
    public AccountPushNotificationStatusResponse updatePushNotificationStatus(boolean isReceived) {
        Account loginAccount = getCurrentAccount();
        loginAccount.updatePushNotificationStatus(isReceived);
        return new AccountPushNotificationStatusResponse(loginAccount.isReceivedPushNotification());
    }

    @Transactional
    public AuthMessage revoke() {
        Account myAccount = getCurrentAccount();
        String socialName = myAccount.getSocialType().getSocialName();
        String refreshToken = myAccount.getProviderRefreshToken();
        String[] split = myAccount.getEmail().split("@");
        boolean result;
        if (socialName.equalsIgnoreCase("Google")) {
            result = googleService.revokeGoogle(refreshToken);
        } else if (socialName.equalsIgnoreCase("Kakao")) {
            result = kakaoService.revokeKakao(split[0]);
        } else if (socialName.equalsIgnoreCase("Apple")) {
            result = appleService.revokeApple(refreshToken);
        } else {
            throw new AccountException(StatusCode.SOCIAL_TYPE_ERROR);
        }
        return new AuthMessage(result, "revoke result.");
    }

    @Transactional
    public AccountCategoryResponse createAccountCategory(AccountCategoryRequest accountCategoryRequest) {
        Account myAccount = getCurrentAccount();
        List<AccountCategory> accountCategoryList = new ArrayList<>();
        for (Long id : accountCategoryRequest.subCategories()) {
            SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow(() -> new CategoryException(StatusCode.NOT_FOUND_CATEGORY));
            AccountCategory accountCategory = AccountCategory.builder().account(myAccount).categorySub(subCategory).build();
            accountCategoryList.add(accountCategory);
        }
        myAccount.setAccountCategory(accountCategoryList);
        return AccountCategoryResponse.of(myAccount);
    }

    public AccountCategoryResponse getAccountCategory() {
        return AccountCategoryResponse.of(getCurrentAccount());
    }

    public AccountListResponse getAccountList(Pageable pageable) {
        Account myAccount = getCurrentAccount();
        if (!myAccount.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccountException(StatusCode.ROLE_ACCESS_ERROR);
        }
        Page<AccountInfoWeb> accountList = accountRepository.findAll(pageable).map(AccountInfoWeb::of);
        return AccountListResponse.of(accountList);
    }

    public AccountListResponse searchAccountList(String startedAt, String endedAt, String keyword, Pageable pageable) {
        Account myAccount = getCurrentAccount();
        if (!myAccount.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccountException(StatusCode.ROLE_ACCESS_ERROR);
        }
        Page<AccountInfoWeb> accountList = accountRepository.findBySearch(startedAt, endedAt, keyword, pageable).map(AccountInfoWeb::of);
        return AccountListResponse.of(accountList);
    }


    // 회원 영구 정지 해제
    @Transactional
    public AccountIdResponse unSuspendAccount(Account loginAccount, Long accountId) {
        loginAccount.isAdmin();
        SuspendAccount suspendAccount = suspendAccountRepository.findByAccountId(accountId).orElseThrow(() -> new ReportException(StatusCode.NOT_FOUND_SUSPEND_ACCOUNT));
        suspendAccount.setSuspended(false);
        Account account = accountRepository.findById(suspendAccount.getAccountId()).orElseThrow(() -> new ReportException(StatusCode.ALREADY_REVOKE_SUSPEND_ACCOUNT));
        account.updateAccountStatus(NORMAL);
        return AccountIdResponse.of(account);
    }

    // 회원 상태 변경
    @Transactional
    public AccountIdResponse updateAccountStatus(Account loginAccount, Long reportedAccountId, String status) {
        loginAccount.isAdmin();
        Account reportedAccount = getAccountById(reportedAccountId);
        validateAccountNotPermanentlyBanned(reportedAccount);
        handlePermanentlyBannedStatus(reportedAccount, status, loginAccount);
        reportedAccount.updateAccountStatus(AccountStatus.valueOf(status));
        return AccountIdResponse.of(reportedAccount);
    }

    private Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new AccountException(StatusCode.NOT_FOUND_ACCOUNT));
    }

    private void validateAccountNotPermanentlyBanned(Account reportedAccount) {
        if (reportedAccount.getAccountStatus() == AccountStatus.PERMANENTLY_BANNED) {
            throw new ReportException(StatusCode.CANNOT_CHANGE_PERMANENTLY_BANNED_ACCOUNT);
        }
    }

    private void handlePermanentlyBannedStatus(Account reportedAccount, String status, Account loginAccount) {
        AccountStatus accountStatus = AccountStatus.valueOf(status);
        switch (accountStatus) {
            case PERMANENTLY_BANNED -> {
                SuspendAccount suspendAccount = createSuspendAccount(reportedAccount);
                suspendAccount.setSuspendedDate();
                suspendAccountRepository.save(suspendAccount);
                reportService.adminReportAccount(loginAccount, reportedAccount, REPORT_TITLE_ADMIN, ReportStatusMessage.PERMANENTLY_BANNED, BAN_PERMANENTLY);
            }
            case BANNED ->
                    reportService.adminReportAccount(loginAccount, reportedAccount, REPORT_TITLE_ADMIN, ReportStatusMessage.BANNED, BAN_ONE_MONTH);
            case PAUSED ->
                    reportService.adminReportAccount(loginAccount, reportedAccount, REPORT_TITLE_ADMIN, ReportStatusMessage.PAUSED, BAN_ONE_MONTH);
            case NORMAL ->
                    reportService.adminReportAccount(loginAccount, reportedAccount, REPORT_TITLE_ADMIN, ReportStatusMessage.NORMAL, UNBAN);
            default -> throw new ReportException(StatusCode.INVALID_ACCOUNT_STATUS);
        }
    }

    private SuspendAccount createSuspendAccount(Account account) {
        return SuspendAccount.builder().accountId(account.getId()).accountEmail(account.getEmail()).accountDI(account.getEmail().split("@")[0]).isSuspended(true).build();
    }

    public SmsResponse sendSms(SmsRequest smsRequest) throws JsonProcessingException, RestClientException, URISyntaxException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        List<SmsRequest> messages = new ArrayList<>();
        messages.add(smsRequest);
        String smsKey = createSmsKey();
        String content = "[PLUB] 본인확인 인증번호는 [" + smsKey + "] 입니다.";
        SmsRequestDTO request = SmsRequestDTO.of(phone, content, messages);

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SmsResponse response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + serviceId + "/messages"), httpBody, SmsResponse.class);

        redisService.createSmsCertification(smsRequest.to(), smsKey);
        return response;
    }

    public SmsMessage certifySms(CertifySmsRequest certifySmsRequest) {
        if (!redisService.hasKey(certifySmsRequest.phone()))
            throw new AccountException(StatusCode.NOT_FOUND_SMS_KEY);
        if (redisService.getSmsCertification(certifySmsRequest.phone()).equals(certifySmsRequest.certificationNum())) {
            redisService.deleteSmsCertification(certifySmsRequest.phone());
            return new SmsMessage("certify success");
        }
        throw new AccountException(StatusCode.INVALID_SMS_KEY);
    }


    public String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + this.serviceId + "/messages";
        String timestamp = time.toString();
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder().append(method).append(space).append(url).append(newLine).append(timestamp).append(newLine).append(accessKey).toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    public static String createSmsKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < 6; i++)
            key.append((rnd.nextInt(10)));
        return key.toString();
    }
}
