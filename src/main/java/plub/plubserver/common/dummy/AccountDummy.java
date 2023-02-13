package plub.plubserver.common.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;

import javax.annotation.PostConstruct;

import static plub.plubserver.common.dummy.DummyImage.PLUB_MAIN_LOGO;
import static plub.plubserver.common.dummy.DummyImage.PLUB_PROFILE_TEST;
import static plub.plubserver.domain.account.dto.AuthDto.SignUpRequest;

@Slf4j
@Component("accountDummy")
@RequiredArgsConstructor
@Transactional
public class AccountDummy {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.secret}")
    private CharSequence ADMIN_PASSWORD;

    @PostConstruct
    public void init() {
        if (accountRepository.count() > 0) {
            log.info("[0] 어드민, 유저가 이미 존재하여 더미를 생성하지 않았습니다.");
            return;
        }

        // 테스트용 계정 - 어드민
        SignUpRequest adminAccount = SignUpRequest.builder()
                .profileImage(PLUB_MAIN_LOGO)
                .build();
        Account account = adminAccount.toAdmin(passwordEncoder, ADMIN_PASSWORD, "admin1");
        accountRepository.save(account);

        SignUpRequest adminAccount2 = SignUpRequest.builder()
                .profileImage(PLUB_MAIN_LOGO)
                .build();
        Account account2 = adminAccount2.toAdmin(passwordEncoder, ADMIN_PASSWORD, "admin2");
        accountRepository.save(account2);

        // 테스트용 계정 - 더미 유저
        for (int i = 0; i < 20; i++) {
            SignUpRequest dummyAccountForm = SignUpRequest.builder()
                    .profileImage(PLUB_PROFILE_TEST)
                    .build();
            accountRepository.save(dummyAccountForm.toDummy(passwordEncoder, ADMIN_PASSWORD, "dummy" + i));
        }

        log.info("[0] 어드민, 유저 더미 생성 완료.");
    }
}
