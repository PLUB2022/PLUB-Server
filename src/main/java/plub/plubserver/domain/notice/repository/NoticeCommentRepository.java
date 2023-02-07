package plub.plubserver.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plub.plubserver.domain.notice.model.Notice;
import plub.plubserver.domain.notice.model.NoticeComment;

import java.util.List;

public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long> {
    List<NoticeComment> findAllByNotice(Notice notice);
}

