package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.config.AccountCode;
import plub.plubserver.domain.account.dto.AccountDto.AccountProfileRequest;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.util.s3.AwsS3Service;

import java.util.regex.Pattern;

import static plub.plubserver.config.security.SecurityUtils.getCurrentAccountEmail;
import static plub.plubserver.domain.account.dto.AccountDto.AccountInfoResponse;
import static plub.plubserver.domain.account.dto.AuthDto.AuthMessage;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;
    private final AppleService appleService;
    private final GoogleService googleService;
    private final KakaoService kakaoService;
    private final AwsS3Service awsS3Service;


    // 회원 정보 조회
    public AccountInfoResponse getMyAccount() {
        return accountRepository.findByEmail(getCurrentAccountEmail())
                .map(AccountInfoResponse::of)
                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));
    }

    public AccountInfoResponse getAccount(String nickname) {
        return accountRepository.findByNickname(nickname)
                .map(AccountInfoResponse::of)
                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));
    }

    public Account getCurrentAccount() {
        return accountRepository.findByEmail(getCurrentAccountEmail())
                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));
    }

    public boolean isDuplicateNickname(String nickname) {
        String pattern = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$";
        if (!Pattern.matches(pattern, nickname)) {
            throw new AccountException(AccountCode.NICKNAME_DUPLICATION);
        }
        return accountRepository.existsByNickname(nickname);
    }

    // 회원 정보 수정
    @Transactional
    public AccountInfoResponse updateProfile(AccountProfileRequest profileRequest) {
        Account myAccount = getCurrentAccount();
        if (isDuplicateNickname(profileRequest.nickname()))
            throw new AccountException(AccountCode.NICKNAME_DUPLICATION);

        myAccount.updateProfile(profileRequest.nickname(), profileRequest.introduce(), profileRequest.profileImageUrl());
        return AccountInfoResponse.of(myAccount);
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
            throw new AccountException(AccountCode.SOCIAL_TYPE_ERROR);
        }
        return new AuthMessage(
                AccountCode.ACCOUNT_SUCCESS.getStatusCode(),
                result,
                "revoke result."
        );
    }
}
