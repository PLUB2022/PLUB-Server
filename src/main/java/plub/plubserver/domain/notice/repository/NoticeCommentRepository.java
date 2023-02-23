package plub.plubserver.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.notice.model.NoticeComment;

public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long>, NoticeCommentRepositoryCustom {
}