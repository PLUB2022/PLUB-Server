package plub.plubserver.domain.recruit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.account.model.Account;
import plub.plubserver.domain.recruit.model.Bookmark;
import plub.plubserver.domain.recruit.model.Recruit;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
    Boolean existsByAccountAndRecruit(Account account, Recruit recruit);
}
