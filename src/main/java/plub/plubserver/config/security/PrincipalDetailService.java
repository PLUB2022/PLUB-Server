package plub.plubserver.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.account.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class PrincipalDetailService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account principal = accountRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. : " + email));
        return new PrincipalDetails(principal);
    }
}
