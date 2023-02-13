package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.config.jwt.JwtDto;
import plub.plubserver.config.jwt.JwtProvider;
import plub.plubserver.config.jwt.RefreshToken;
import plub.plubserver.config.jwt.RefreshTokenRepository;
import plub.plubserver.config.security.PrincipalDetails;
import plub.plubserver.domain.account.config.AccountCode;
import plub.plubserver.domain.account.config.AuthCode;
import plub.plubserver.domain.account.exception.AccountException;
import plub.plubserver.domain.account.exception.AuthException;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.model.AccountCategory;
import plub.plubserver.domain.account.model.Role;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.domain.category.config.CategoryCode;
import plub.plubserver.domain.category.exception.CategoryException;
import plub.plubserver.domain.category.model.SubCategory;
import plub.plubserver.domain.category.repository.SubCategoryRepository;
import plub.plubserver.domain.policy.model.Policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static plub.plubserver.config.security.SecurityUtils.getCurrentAccountEmail;
import static plub.plubserver.domain.account.dto.AuthDto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AppleService appleService;
    private final GoogleService googleService;
    private final KakaoService kakaoService;
    private final SubCategoryRepository subCategoryRepository;

    public AuthMessage loginAccess(SocialLoginRequest socialLoginRequest) {
        OAuthIdAndRefreshTokenResponse response = fetchSocialEmail(socialLoginRequest);
        String email = response.id();
        String refreshToken = response.refreshToken();
        Optional<Account> account = accountRepository.findByEmail(email);
        AuthMessage loginMessage;
        if (account.isPresent()) {
            JwtDto jwtDto = login(LoginRequest.toLoginRequest(account.get()));
            account.get().updateRefreshToken(refreshToken);
            loginMessage = new AuthMessage(
                    jwtDto,
                    AuthCode.LOGIN.getMessage()
            );
        } else {
            String signToken = jwtProvider.createSignToken(email, refreshToken);
            SignToken signTokenResponse = new SignToken(signToken);
            throw new AuthException(signTokenResponse, AuthCode.NEED_TO_SIGNUP);
        }
        return loginMessage;
    }


    public JwtDto login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = loginRequest.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Account account = principal.getAccount();
        return jwtProvider.issue(account);
    }

    @Transactional
    public SignAuthMessage signUp(SignUpRequest signUpRequest) {
        String signToken = signUpRequest.signToken();
        if (!jwtProvider.validate(signToken))
            throw new AuthException(AuthCode.SIGNUP_TOKEN_ERROR);

        SigningAccount signKey = jwtProvider.getSignKey(signToken);
        String email = signKey.email() + "@" + signKey.socialType();
        String socialType = signKey.socialType();
        String refreshToken = signKey.refreshToken();
        String nickname = signUpRequest.nickname();
        String profileImage = signUpRequest.profileImage();

        boolean agePolicy = signUpRequest.agePolicy();
        boolean personalPolicy = signUpRequest.personalPolicy();
        boolean placePolicy = signUpRequest.placePolicy();
        boolean usePolicy = signUpRequest.usePolicy();
        boolean marketPolicy = signUpRequest.marketPolicy();

        ConcurrentHashMap<String, Boolean> policyCheckList = new ConcurrentHashMap<>();
        policyCheckList.put("agePolicy", agePolicy);
        policyCheckList.put("personalPolicy", personalPolicy);
        policyCheckList.put("placePolicy", placePolicy);
        policyCheckList.put("usePolicy", usePolicy);
        policyCheckList.put("marketPolicy", marketPolicy);

        checkDuplicationEmailAndNickName(email, nickname);
        Account account = signUpRequest.toAccount(email, socialType, passwordEncoder);

        // 정책 리스트
        List<Policy> policyList = new ArrayList<>();
        policyCheckList.forEach((title, isChecked) -> {
            Policy policy = Policy.builder()
                    .title(title)
                    .isChecked(isChecked)
                    .account(account)
                    .build();

            policyList.add(policy);
        });
        account.updateAccountPolicy(policyList);


        // 카테고리 리스트
        List<Long> categoryList = signUpRequest.categoryList();
        List<AccountCategory> accountCategoryList = new ArrayList<>();
        for (Long id : categoryList) {
            SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow(() -> new CategoryException(CategoryCode.NOT_FOUND_CATEGORY));
            AccountCategory accountCategory = AccountCategory.builder()
                    .account(account)
                    .categorySub(subCategory)
                    .build();
            accountCategoryList.add(accountCategory);
        }

        accountRepository.save(account);

        account.updateAccountCategory(accountCategoryList);
        account.updateProfileImage(profileImage);

        JwtDto jwtDto = login(LoginRequest.toLoginRequest(account));
        account.updateRefreshToken(refreshToken);

        return new SignAuthMessage(
                jwtDto,
                AuthCode.SIGNUP_COMPLETE.getMessage()
        );
    }

    private void checkDuplicationEmailAndNickName(String email, String nickname) {
        if (accountRepository.existsByEmail(email)) {
            throw new AccountException(AccountCode.EMAIL_DUPLICATION);
        }
        if (accountRepository.existsByNickname(nickname)) {
            throw new AccountException(AccountCode.NICKNAME_DUPLICATION);
        }
    }

    private OAuthIdAndRefreshTokenResponse fetchSocialEmail(SocialLoginRequest socialLoginRequest) {
        String provider = socialLoginRequest.socialType();
        if (provider.equalsIgnoreCase("Google")) {
            return googleService.requestGoogleToken(socialLoginRequest.authorizationCode());
        } else if (provider.equalsIgnoreCase("Kakao")) {
            return kakaoService.getKakaoId(socialLoginRequest.accessToken());
        } else {
            return appleService.requestAppleToken(socialLoginRequest);
        }
    }

    public JwtDto reissue(ReissueRequest reissueDto) {
        return jwtProvider.reissue(reissueDto.refreshToken());
    }

    @Transactional
    public String logout() {
        Account account = accountRepository
                .findByEmail(getCurrentAccountEmail())
                .orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));
        RefreshToken refreshToken = refreshTokenRepository
                .findByAccount(account)
                .orElseThrow(() -> new AuthException(AuthCode.NOT_FOUND_REFRESH_TOKEN));
        refreshTokenRepository.delete(refreshToken);
        refreshTokenRepository.flush();
        return "로그아웃 완료";
    }

    public AuthMessage loginAdmin(LoginRequest loginRequest) {
        String email = loginRequest.email();
        Account admin = accountRepository.findByEmail(email).orElseThrow(() -> new AccountException(AccountCode.NOT_FOUND_ACCOUNT));
        if (!admin.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccountException(AccountCode.ROLE_ACCESS_ERROR);
        }
        JwtDto jwtDto = login(loginRequest);
        return new AuthMessage(
                jwtDto,
                AuthCode.LOGIN.getMessage()
        );
    }
}
