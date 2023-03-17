package plub.plubserver.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.notice.model.NoticeLike;


public interface NoticeLikeRepository extends JpaRepository<NoticeLike, Long> {
    boolean existsByAccountAndNotice(Account account, Notice notice);

    void deleteByAccountAndNotice(Account account, Notice notice);

    Long countAllByVisibilityAndNotice(boolean visibility, Notice notice);
}

