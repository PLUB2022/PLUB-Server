package plub.plubserver.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.notice.model.NoticeComment;

import java.util.Optional;

public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long>, NoticeCommentRepositoryCustom {
    Long countAllByVisibilityAndNotice(boolean visibility, Notice notice);

    Optional<NoticeComment> findFirstByVisibilityAndNoticeId(boolean visibility, Long noticeId);

}