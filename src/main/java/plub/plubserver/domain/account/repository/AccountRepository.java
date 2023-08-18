package plub.plubserver.domain.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import plub.plubserver.domain.account.model.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryCustom {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByNickname(String nickname);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByPhone(String phone);

    /**
     * 관리자페이지
     */
    @Query("select count(a) from Account a where a.createdAt like concat(:createdAt, '%')")
    Long countByCreatedAt(@Param("createdAt") String createdAt);

    @Query("select count(a) from Account a where a.createdAt like %:thisMonth%")
    Long countByCreatedAtMonthly(@Param("thisMonth") String thisMonth);

    boolean existsByPhone(String phone);
}
