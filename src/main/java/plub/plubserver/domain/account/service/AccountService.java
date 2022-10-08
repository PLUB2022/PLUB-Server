package plub.plubserver.domain.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.config.security.SecurityUtils;
import plub.plubserver.domain.account.dto.AccountDto;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;
import plub.plubserver.exception.AccountException;

import static plub.plubserver.domain.account.dto.AccountDto.AccountInfo;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

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

}
