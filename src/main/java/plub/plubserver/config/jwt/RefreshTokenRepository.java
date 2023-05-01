package plub.plubserver.config.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    Optional<RefreshToken> findByAccount(Account account);

    void deleteByAccount(Account account);

    boolean existsByAccount(Account account);
}
