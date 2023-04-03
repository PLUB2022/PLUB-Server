package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.common.exception.StatusCode;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.AccountCategory;
import plub.plubserver.domain.account.model.Role;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.domain.category.exception.CategoryException;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.category.repository.SubCategoryRepository;
import plub.plubserver.domain.report.model.Report;
import plub.plubserver.domain.report.service.ReportService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static plub.plubserver.config.security.SecurityUtils.getCurrentAccountEmail;
import static plub.plubserver.domain.account.dto.AccountDto.*;
import static plub.plubserver.domain.account.dto.AuthDto.AuthMessage;
import static plub.plubserver.domain.report.dto.ReportDto.CreateReportRequest;
import static plub.plubserver.domain.report.dto.ReportDto.ReportResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final AppleService appleService;
    private final GoogleService googleService;
    private final KakaoService kakaoService;

    private final ReportService reportService;


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
        String pattern = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$";
        if (!Pattern.matches(pattern, nickname)) {
            throw new AccountException(StatusCode.NICKNAME_ERROR);
        }
        if (!accountRepository.existsByNickname(nickname)) {
            return new NicknameResponse(true);
        } else throw new AccountException(StatusCode.NICKNAME_DUPLICATION);
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public AccountInfoResponse updateProfile(AccountProfileRequest profileRequest) {
        Account myAccount = getCurrentAccount();
        if (!myAccount.getNickname().equals(profileRequest.nickname())) {
            NicknameResponse duplicateNickname = isDuplicateNickname(profileRequest.nickname());
            if (!duplicateNickname.isAvailableNickname()) throw new AccountException(StatusCode.NICKNAME_DUPLICATION);
        }
        myAccount.updateProfile(profileRequest.nickname(), profileRequest.introduce(), profileRequest.profileImageUrl());
        return AccountInfoResponse.of(myAccount);
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

    /**
     * 회원 신고
     */
    @Transactional
    public ReportResponse reportAccount(Account reporter, Long accountId, CreateReportRequest createReportRequest) {
        Account ReportedAccount = getAccount(accountId);
        if (ReportedAccount.getId().equals(reporter.getId())) {
            throw new AccountException(StatusCode.SELF_REPORT_ERROR);
        }
        Report report = reportService.createReport(createReportRequest, reporter);
        return reportService.notifyAccount(report, ReportedAccount);
    }
}
