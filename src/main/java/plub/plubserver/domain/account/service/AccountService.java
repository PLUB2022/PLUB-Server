package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
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

import static plub.plubserver.domain.account.dto.AccountDto.AccountInfo;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AppleService appleService;
    private final RestTemplate restTemplate;

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

    // 탈퇴
    public AuthDto.AuthMessage revoke(AuthDto.RevokeRequest revokeAccount) throws IOException {
        Account myAccount = accountRepository.findByEmail(SecurityUtils.getCurrentAccountEmail())
                .orElseThrow(() -> new AccountException("회원 정보 없음"));
        String socialName = myAccount.getSocialType().getSocialName();
        if (socialName.equalsIgnoreCase("Google")) {
            String accessToken = revokeAccount.accessToken();
            System.out.println("accessToken = " + accessToken);
            System.out.println(1);
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            System.out.println(2);
            parameters.add("token", accessToken);
            System.out.println(3);
            restTemplate.postForEntity("https://oauth2.googleapis.com/revoke",
                    parameters,
                    String.class
            );
            System.out.println(4);
        } else if (socialName.equalsIgnoreCase("Kakao")) {

        } else {
            // apple 한 번 로그인 후 authorization_code 가져오기
            // apple 연결 해제
            appleService.revokeApple(myAccount, revokeAccount.authorizationCode());
            // 삭제
        }
        return new AuthDto.AuthMessage("d", "탈퇴완료");
    }

}
