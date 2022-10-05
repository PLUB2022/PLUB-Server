package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import plub.plubserver.config.security.SecurityUtils;
import plub.plubserver.domain.account.dto.AccountDto;
import plub.plubserver.domain.account.dto.AuthDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.exception.AccountException;

import java.io.IOException;
import java.util.Collections;

import static plub.plubserver.domain.account.dto.AccountDto.AccountInfo;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AppleService appleService;
    private final RestTemplate restTemplate;

    @Value("${value.appAdminKey}")
    private String appAdminKey;

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public AccountInfo getMyAccount() {
        return accountRepository.findByEmail(SecurityUtils.getCurrentAccountEmail())
                .map(AccountInfo::of).orElseThrow(()-> new AccountException("회원 정보 없음"));
    }

    @Transactional(readOnly = true)
    public AccountInfo getAccount(String nickname) {
        return accountRepository.findByNickname(nickname)
                .map(AccountInfo::of).orElseThrow(() -> new AccountException("회원 정보 없음"));
    }

    // 회원 정보 수정
    @Transactional
    public AccountInfo updateNickname(AccountDto.AccountNicknameRequest request) {
        Account myAccount = accountRepository.findByEmail(SecurityUtils.getCurrentAccountEmail())
                .orElseThrow(() -> new AccountException("회원 정보 없음"));
        duplicateNickname(request.nickname());
        myAccount.updateNickname(request.nickname());
        return AccountInfo.of(myAccount);
    }

    private void duplicateNickname(String nickname) {
        if (accountRepository.existsByNickname(nickname)) {
            throw new AccountException("nickname 중복 입니다.");
        }
    }

    @Transactional
    public AccountInfo updateIntroduce(AccountDto.AccountIntroduceRequest request) {
        Account myAccount = accountRepository.findByEmail(SecurityUtils.getCurrentAccountEmail())
                .orElseThrow(() -> new AccountException("회원 정보 없음"));
        myAccount.updateIntroduce(request.introduce());
        return AccountInfo.of(myAccount);
    }

    @Transactional
    public AuthDto.AuthMessage revoke(AuthDto.RevokeRequest revokeAccount) throws IOException {
        Account myAccount = accountRepository.findByEmail(SecurityUtils.getCurrentAccountEmail())
                .orElseThrow(() -> new AccountException("회원 정보 없음"));
        String socialName = myAccount.getSocialType().getSocialName();

        if (socialName.equalsIgnoreCase("Google")) {
            revokeGoogle(revokeAccount);
        } else if (socialName.equalsIgnoreCase("Kakao")) {
            revokeKakao(revokeAccount);
        } else {
            appleService.revokeApple(myAccount, revokeAccount.authorizationCode());
        }
        return new AuthDto.AuthMessage("d", "탈퇴완료");
    }

    private void revokeGoogle(AuthDto.RevokeRequest revokeAccount) {
        String accessToken = revokeAccount.accessToken();
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("token", accessToken);
        restTemplate.postForEntity("https://oauth2.googleapis.com/revoke", parameters, String.class);
    }

    private void revokeKakao(AuthDto.RevokeRequest revokeAccount) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", revokeAccount.userId());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + appAdminKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        restTemplate.postForEntity("https://kapi.kakao.com/v1/user/unlink", httpEntity, AuthDto.RevokeResponseKakao.class);
    }

}
